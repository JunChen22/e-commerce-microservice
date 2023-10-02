package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.config.PaypalPaymentIntent;
import com.itsthatjun.ecommerce.config.PaypalPaymentMethod;
import com.itsthatjun.ecommerce.dao.OrderDao;
import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsCouponOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsSalesStockOutEvent;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.exceptions.OrderException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.OrderService;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static com.itsthatjun.ecommerce.dto.event.outgoing.SmsCouponOutEvent.Type.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final double SHIPPING_COST = 15;  // default shipping cost for order less than 50

    private final PaypalService paypalService;

    private final OrdersMapper ordersMapper;

    private final CartItemServiceImpl cartItemService;

    private final OrderItemMapper orderItemMapper;

    private final ProductSkuMapper stockMapper;

    private final ProductMapper productMapper;

    private final OrderChangeHistoryMapper changeHistoryMapper;

    private final OrderDao orderDao;

    private final StreamBridge streamBridge;

    private final String SMS_SERVICE_URL = "http://sms:8080/coupon";

    private final WebClient webClient;

    @Autowired
    public OrderServiceImpl(PaypalService paypalService, OrdersMapper ordersMapper, CartItemServiceImpl cartItemService,
                            OrderItemMapper orderItemMapper, ProductSkuMapper stockMapper, ProductMapper productMapper,
                            OrderChangeHistoryMapper changeHistoryMapper, OrderDao orderDao, StreamBridge streamBridge,
                            WebClient.Builder webClient) {
        this.paypalService = paypalService;
        this.ordersMapper = ordersMapper;
        this.cartItemService = cartItemService;
        this.orderItemMapper = orderItemMapper;
        this.stockMapper = stockMapper;
        this.productMapper = productMapper;
        this.changeHistoryMapper = changeHistoryMapper;
        this.orderDao = orderDao;
        this.streamBridge = streamBridge;
        this.webClient = webClient.build();
    }

    @Override
    public Mono<OrderDetail> getOrdeDetail(String orderSn) {
        OrderDetail orderDetail = orderDao.getDetail(orderSn);

        if (orderDetail == null) {
            return Mono.just(orderDetail);
        } else {
            return Mono.error(new OrderException("Order number does not exist: " + orderSn));
        }
    }

    @Override
    public Flux<Orders> list(int status, int pageNum, int pageSize, int userId) {
        PageHelper.startPage(pageNum, pageSize);
        OrdersExample ordersExample = new OrdersExample();
        // TODO: the status code. currently just return all
        ordersExample.createCriteria().andMemberIdEqualTo(userId);

        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        return Flux.fromIterable(ordersList);
    }

    @Override
    public Mono<Orders> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId) {

        String orderSn = generateOrderSn();
        String couponCode = orderParam.getCoupon();
        Address address = orderParam.getAddress();
        String payType = orderParam.getPayType();

        Orders newOrder = new Orders();
        List<OrderItem> orderItemList = new ArrayList<>();

        Map<String, Integer> skuQuantity = orderParam.getOrderProductSku(); // sku and quantity for order

        double orderTotal = 0;
        double couponDiscount = orderParam.getDiscountAmount();

        // verify the price again
        for (String productSku: skuQuantity.keySet()) {

            int quantity = orderParam.getOrderProductSku().get(productSku);

            // find the sku and product
            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(productSku);
            ProductSku productSkuStock = stockMapper.selectByExample(productSkuExample).get(0);

            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(productSkuStock.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductQuantity(quantity);
            orderItem.setProductSkuCode(productSku);

            orderTotal += productSkuStock.getPromotionPrice().doubleValue() * quantity;
            orderItemList.add(orderItem);
        }

        if (!hasStock(orderItemList)) {
            throw new OrderException("Not enough stock, Unable to order");
        }

        orderTotal = Math.round(orderTotal * 100.0) / 100.0;  // TODO: change back double to big decimal for more accuracy
        // and use.setScale(2, RoundingMode.CEILING);

        // verify price difference
        if (orderTotal != orderParam.getAmount()) {
            throw new OrderException("Pricing error");
        }

        lockStock(orderItemList);

        // synchronous call to coupon service, assume the accepted discount amount is incorrect.
        double checkedDiscount = checkCouponDiscountFromSms(couponCode, userId);
        couponDiscount = couponDiscount == checkedDiscount ? couponDiscount : 0;

        // check shipping cost, $50 or more get free shipping or $15
        // TODO: calculate shipping cost from UPS API, currently just fixed rate
        if (orderTotal < 50) orderTotal += SHIPPING_COST;

        orderTotal = orderTotal - couponDiscount;

        //newOrder.setPayType(payType);  // TODO: change data type to string in database

        newOrder.setOrderSn(orderSn);
        newOrder.setStatus(0);   // waiting for payment
        newOrder.setTotalAmount(BigDecimal.valueOf(orderTotal));
        newOrder.setMemberId(userId);

        newOrder.setReceiverName(address.getReceiverName());
        newOrder.setReceiverDetailAddress(address.getDetailAddress());

        ordersMapper.insert(newOrder);

        int newOrderId = newOrder.getId();

        for (OrderItem item : orderItemList) {
            item.setOrderSn(orderSn);
            item.setOrderId(newOrderId);
            orderItemMapper.insert(item);
        }

        // Generated order, increase sku lock stock
        sendProductStockUpdateMessage("product-out-0", new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));

        if (!couponCode.equals("")) { // update coupon usage, won't go up even return or payment fail
            sendCouponUpdateMessage("coupon-out-0", new SmsCouponOutEvent(UPDATE_COUPON_USAGE, couponCode, userId, newOrderId));
        }

        // clear cart
        cartItemService.clearCartItem(userId);

        updateChangeLog(newOrderId, 0, "generate order","user");

        try {
            Payment payment = paypalService.createPayment(orderTotal, "USD", PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale, "payment description", cancelUrl ,
                    successUrl, orderSn);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    System.out.println("redirect:" + links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            LOG.error(e.getMessage());
        }

        if (newOrder != null) {
            return Mono.just(newOrder);
        } else {
            return Mono.error(new OrderException("error creating new order"));
        }
    }

    private double checkCouponDiscountFromSms(String couponCode, int userId) {
        String url = SMS_SERVICE_URL + "/check?couponCode=" + couponCode;
        LOG.debug("Will call the list API on URL: {}", url);

        // Define the timeout in milliseconds
        int timeoutMilliseconds = 200; // 0.2 second

        double discountAmount = 0;

        try {
            // Make a synchronous HTTP GET request with a timeout to the coupon service
            Mono<Double> discountMono = webClient.get()
                    .uri(url)
                    .header("X-UserId", String.valueOf(userId))
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("Server error")))
                    .bodyToMono(Double.class)
                    .timeout(Duration.ofMillis(timeoutMilliseconds));

            discountAmount = discountMono.blockOptional().orElse(0.0);
        } catch (Exception e) {
            // Handle errors, including timeouts, here
            discountAmount = 0;
        }

        return discountAmount;
    }

    @Override
    public Mono<Orders> paySuccess(String orderSn, String paymentId, String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                System.out.println("successful payment");

                // find and update the order, status, payment info
                OrdersExample ordersExample = new OrdersExample();
                ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
                Orders foundOrder = ordersMapper.selectByExample(ordersExample).get(0);

                foundOrder.setStatus(1);  // waiting for payment 0 , fulfilling 1,
                foundOrder.setPaymentId(paymentId);
                foundOrder.setPayerId(payerId);
                foundOrder.setPayAmount(foundOrder.getTotalAmount());

                int orderId = foundOrder.getId();
                Date currentDate = new Date();
                foundOrder.setPaymentTime(currentDate);
                foundOrder.setUpdatedAt(currentDate);

                ordersMapper.updateByPrimaryKey(foundOrder);

                OrderItemExample orderItemExample = new OrderItemExample();
                orderItemExample.createCriteria().andOrderSnEqualTo(orderSn);
                List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

                Map<String, Integer> itemOrderQuantity = new HashMap<>();

                // update stock and free up locked stock
                for (OrderItem item: orderItemList) {
                    int quantityOrdered = item.getProductQuantity();;

                    // find product and get current stock and update it
                    ProductExample productExample = new ProductExample();
                    productExample.createCriteria().andIdEqualTo(item.getProductId());
                    Product product = productMapper.selectByExample(productExample).get(0);

                    int currentStock = product.getStock();
                    product.setStock(currentStock - quantityOrdered);
                    productMapper.updateByPrimaryKey(product);

                    // update sku stock and free lock stock
                    ProductSkuExample productSkuStockExample = new ProductSkuExample();
                    productSkuStockExample.createCriteria().andSkuCodeEqualTo(item.getProductSkuCode()).andProductIdEqualTo(product.getId());
                    ProductSku producutSku = stockMapper.selectByExample(productSkuStockExample).get(0);

                    currentStock = producutSku.getStock();
                    producutSku.setStock(currentStock - quantityOrdered);
                    producutSku.setLockStock(producutSku.getLockStock() - quantityOrdered);
                    stockMapper.updateByPrimaryKey(producutSku);
                }
                // Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock
                sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));
                sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));

                updateChangeLog(orderId, 1, "payment success","user");

                return Mono.just(foundOrder);
            }
        } catch (PayPalRESTException e) {
            LOG.error(e.getMessage());
        }
        return Mono.error(new OrderException("Error payment after success URL"));
    }

    @Override
    public void payFail(String orderSn) {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        Orders newOrder = ordersMapper.selectByExample(ordersExample).get(0);

        int orderId = newOrder.getId();

        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderSnEqualTo(orderSn).andOrderIdEqualTo(orderId);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

        Map<String, Integer> itemOrderQuantity = new HashMap<>();

        // free up locked stock
        for (OrderItem item : orderItemList) {
            int productId = item.getProductId();
            String skuCode = item.getProductSkuCode();
            int quantityNeeded = item.getProductQuantity();

            ProductSkuExample example = new ProductSkuExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSku skuStock = stockMapper.selectByExample(example).get(0);

            int itemStockLockNow = skuStock.getLockStock();
            skuStock.setLockStock(itemStockLockNow - quantityNeeded);
            stockMapper.updateByPrimaryKey(skuStock);

            itemOrderQuantity.put(skuCode, quantityNeeded);
        }
        // Generated order and failure payment, decrease sku lock stock
        sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));

        updateChangeLog(orderId, 5, "payment fail","user");
    }

    @Override
    public Mono<Orders> cancelOrder(String orderSn) {

        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        if (ordersList.isEmpty()) {
            throw new RuntimeException("order not found with order serial number: " + orderSn); // TODO: create OrderNotFoundException
        }

        Orders foundOrder = ordersList.get(0);
        int orderId = foundOrder.getId();

        //  waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 , invalid 5
        int currentStatus = foundOrder.getStatus();

        if (currentStatus >= 2) {
            throw new OrderException("Order already send out, can not cancel");
        }

        // TODO :might add something like need admin approval after certain hours to cancel order
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

        Map<String, Integer> skuQuantity = new HashMap<>();

        // record order sku and quantity, and update OMS stock.
        for (OrderItem orderItem : orderItemList) {
            String skuCode = orderItem.getProductSkuCode();
            int quantity = orderItem.getProductQuantity();
            skuQuantity.put(skuCode, quantity);

            // update OMS stock, increase product and sku stock. same as return.
            ProductSkuExample skuStockExample = new ProductSkuExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku skuStock = stockMapper.selectByExample(skuStockExample).get(0);

            int currentStock = skuStock.getStock();

            skuStock.setStock(currentStock + quantity);
            stockMapper.updateByPrimaryKey(skuStock);
        }

        sendProductStockUpdateMessage("product-out-0", new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));

        foundOrder.setStatus(5);
        ordersMapper.updateByPrimaryKeySelective(foundOrder);

        updateChangeLog(orderId, 5, "Cancel order","user");

        if (foundOrder != null) {
            return Mono.just(foundOrder);
        } else {
            return Mono.error(new OrderException("unable to cancel order" + orderSn));
        }
    }

    @Override
    public void confirmReceiveOrder(int orderId) {
        // TODO: with spring stask to check from UPS/FedEx for package delivery update.
    }

    // TODO: use a better way to generate order serial number
    private String generateOrderSn() {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //String key = REDIS_DATABASE + ":"+ REDIS_KEY_ORDER_ID + date;
        //Long increment = redisService.incr(key, 1);
        sb.append(date);
        sb.append("A BETTER WAY TO GENERATE SN");
        //sb.append(String.format("%02d", order.getSourceType()));
        //sb.append(String.format("%02d", order.getPayType()));
        //String incrementStr = increment.toString();
        //if (incrementStr.length() <= 6) {
        //    sb.append(String.format("%06d", increment));
        //} else {
        //    sb.append(incrementStr);
        //}
        return sb.toString();
    }

    private void updateChangeLog(int orderId, int status, String note, String operator) {
        OrderChangeHistory changeLog = new OrderChangeHistory();
        changeLog.setOrderId(orderId);
        changeLog.setOrderStatus(status);
        changeLog.setChangeOperator(operator);
        changeLog.setNote(note);
        changeLog.setCreatedAt(new Date());
        changeHistoryMapper.insert(changeLog);
    }

    private void lockStock(List<OrderItem> orderItemList) {

        for (OrderItem item : orderItemList) {
            int productId = item.getProductId();
            String skuCode = item.getProductSkuCode();
            int quantityNeeded = item.getProductQuantity();

            ProductSkuExample example = new ProductSkuExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSku itemStock = stockMapper.selectByExample(example).get(0);

            int itemStockNow = itemStock.getLockStock();

            itemStock.setLockStock(itemStockNow + quantityNeeded);
            stockMapper.updateByPrimaryKey(itemStock);
        }
    }

    private boolean hasStock(List<OrderItem> orderItemList) {

        for (OrderItem item : orderItemList) {
            int productId = item.getProductId();
            String skuCode = item.getProductSkuCode();
            int quantityNeeded = item.getProductQuantity();
            ProductSkuExample example = new ProductSkuExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSku itemStock = stockMapper.selectByExample(example).get(0);

            if (itemStock != null && itemStock.getStock() < (itemStock.getLockStock() + quantityNeeded)){
                return false; // send alert somewhere here to alert admin about low stock
            }
        }
        return true;
    }

    private void sendProductStockUpdateMessage(String bindingName, PmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendCouponUpdateMessage(String bindingName, SmsCouponOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderId())
                .build();
        streamBridge.send(bindingName, message);
    }

    // TODO: change these based of status code and add 4 and 5 in
    // waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5
    @Override
    public Flux<Orders> getAllWaitingForPayment() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(0);
        List<Orders> orderWaitingForPayment = ordersMapper.selectByExample(example);
        return Flux.fromIterable(orderWaitingForPayment);
    }

    @Override
    public Flux<Orders> getAllFulfulling() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(1);
        List<Orders> orderWaitingToBeFulfill = ordersMapper.selectByExample(example);
        return Flux.fromIterable(orderWaitingToBeFulfill);
    }

    @Override
    public Flux<Orders> getAllInSend() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(2);
        List<Orders> orderInTransit = ordersMapper.selectByExample(example);
        return Flux.fromIterable(orderInTransit);
    }

    @Override
    public Flux<Orders> getAllCompleteOrder() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(3);
        List<Orders> orderCompletedOrder = ordersMapper.selectByExample(example);
        return Flux.fromIterable(orderCompletedOrder);
    }

    @Override
    public Flux<Orders> getUserOrders(int memberId) {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<Orders> ordersList = ordersMapper.selectByExample(example);
        return Flux.fromIterable(ordersList);
    }

    @Override
    public Mono<Orders> createOrder(Orders newOrder, List<OrderItem> orderItemList, String reason, String operator) {

        // TODO: make it generate order but still need user to pay.
        //      currently is just replacement parts, free of charge
        //      admin make orders for user.

        //  TODO: need to generate order serial number
        String orderSn = generateOrderSn();
        newOrder.setOrderSn(orderSn);
        newOrder.setStatus(1);
        newOrder.setCreatedAt(new Date());
        newOrder.setAdminNote(reason);
        newOrder.setPayAmount(new BigDecimal(0));

        // TODO: address in here

        // fill in order item info
        for (OrderItem item : orderItemList) {

            int quantity = item.getProductQuantity();
            String productSku = item.getProductSkuCode();

            // find the sku and product
            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(productSku);
            ProductSku productSkuStock = stockMapper.selectByExample(productSkuExample).get(0);

            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(productSkuStock.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductQuantity(quantity);
        }

        if (!hasStock(orderItemList)) {
            throw new OrderException("Not enough stock, Unable to order");
        }

        lockStock(orderItemList);
        ordersMapper.insert(newOrder);

        int orderId = newOrder.getId();
        Map<String, Integer> skuQuantity = new HashMap<>();

        for (OrderItem item : orderItemList) {
            item.setOrderId(orderId);
            orderItemMapper.insert(item);

            String sku = item.getProductSkuCode();
            int quantity = item.getProductQuantity();
            skuQuantity.put(sku, quantity);
        }

        sendProductStockUpdateMessage("product-out-0", new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));

        // free up stock
        for (OrderItem item: orderItemList) {
            int quantityOrdered = item.getProductQuantity();;

            // find product and get current stock and update it
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(item.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            int currentStock = product.getStock();
            product.setStock(currentStock - quantityOrdered);
            productMapper.updateByPrimaryKey(product);

            // update sku stock and free lock stock
            ProductSkuExample productSkuStockExample = new ProductSkuExample();
            productSkuStockExample.createCriteria().andSkuCodeEqualTo(item.getProductSkuCode()).andProductIdEqualTo(product.getId());
            ProductSku producutSku = stockMapper.selectByExample(productSkuStockExample).get(0);

            currentStock = producutSku.getStock();
            producutSku.setStock(currentStock - quantityOrdered);
            producutSku.setLockStock(producutSku.getLockStock() - quantityOrdered);
            stockMapper.updateByPrimaryKey(producutSku);
        }
        sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, skuQuantity));

        OrderChangeHistory changelog = new OrderChangeHistory();

        updateChangeLog(orderId, 1, reason,operator);

        if (newOrder != null) {
            return Mono.just(newOrder);
        } else {
            return Mono.error(new OrderException("Error creating order by" + operator));
        }
    }

    @Override
    public Mono<Orders> updateOrder(Orders updateOrder, String reason, String operator) {

        updateOrder.setUpdatedAt(new Date());
        updateOrder.setAdminNote(reason);
        ordersMapper.updateByPrimaryKey(updateOrder);

        OrderChangeHistory changelog = new OrderChangeHistory();
        int orderId = updateOrder.getId();
        int status = updateOrder.getStatus();

        updateChangeLog(orderId, status, reason,operator);

        if (updateOrder != null) {
            return Mono.just(updateOrder);
        } else {
            return Mono.error(new OrderException("Update order error with order id " + orderId));
        }
    }

    @Override
    public void adminCancelOrder(Orders updateOrder, String reason, String operator) {
        updateOrder.setComment(reason);
        updateOrder.setUpdatedAt(new Date());
        updateOrder.setStatus(4);
        ordersMapper.updateByPrimaryKey(updateOrder);

        OrderChangeHistory changelog = new OrderChangeHistory();
        int orderId = updateOrder.getId();

        updateChangeLog(orderId, 4, reason, operator);
    }
}
