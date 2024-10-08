package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.config.PaypalPaymentIntent;
import com.itsthatjun.ecommerce.config.PaypalPaymentMethod;
import com.itsthatjun.ecommerce.dao.OrderDao;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.dto.event.incoming.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsOrderEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsCouponOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsSalesStockOutEvent;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.OrderDTO;
import com.itsthatjun.ecommerce.exceptions.OrderException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.OrderService;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.itsthatjun.ecommerce.dto.event.incoming.OmsCompletionEvent.Type.PAYMENT_FAILURE;
import static com.itsthatjun.ecommerce.dto.event.outgoing.OmsOrderEvent.Type.*;
import static com.itsthatjun.ecommerce.dto.event.outgoing.SmsCouponOutEvent.Type.UPDATE_COUPON_USAGE;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PaypalService paypalService;

    private final RedisServiceImpl redisService;

    private final OrdersMapper ordersMapper;

    private final CartItemServiceImpl cartItemService;

    private final OrderItemMapper orderItemMapper;

    private final ProductSkuMapper stockMapper;

    private final ProductMapper productMapper;

    private final OrderChangeHistoryMapper changeHistoryMapper;

    private final OrderDao orderDao;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final DTOMapper dtoMapper;

    private final BigDecimal SHIPPING_COST = BigDecimal.valueOf(15);  // default shipping cost for order less than 50

    private final String SMS_SERVICE_URL = "http://sms/coupon";

    private final String PAYPAL_PAYMENT_LINK = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";

    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    private final WebClient webClient;

    @Autowired
    public OrderServiceImpl(PaypalService paypalService, RedisServiceImpl redisService, OrdersMapper ordersMapper,
                            CartItemServiceImpl cartItemService, OrderItemMapper orderItemMapper, ProductSkuMapper stockMapper,
                            ProductMapper productMapper, OrderChangeHistoryMapper changeHistoryMapper, OrderDao orderDao,
                            StreamBridge streamBridge, WebClient.Builder webClient, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
                            DTOMapper dtoMapper) {
        this.paypalService = paypalService;
        this.redisService = redisService;
        this.ordersMapper = ordersMapper;
        this.cartItemService = cartItemService;
        this.orderItemMapper = orderItemMapper;
        this.stockMapper = stockMapper;
        this.productMapper = productMapper;
        this.changeHistoryMapper = changeHistoryMapper;
        this.orderDao = orderDao;
        this.streamBridge = streamBridge;
        this.webClient = webClient.build();
        this.jdbcScheduler = jdbcScheduler;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Mono<OrderDetail> getOrderDetail(String orderSn, int userId) {
        return Mono.fromCallable(() -> {
            OrderDetail orderDetail = orderDao.getDetail(orderSn, userId);
            return orderDetail;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<OrderDTO> list(int status, int pageNum, int pageSize, int userId) {
        return Mono.fromCallable(() -> {
            PageHelper.startPage(pageNum, pageSize);
            // TODO: the status code. currently just return all
            List<OrderDTO> ordersList = orderDao.getUserAllOrders(userId);
            return ordersList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<String> getPaymentLink(String orderSn, int userId) {
        return Mono.fromCallable(() -> {
            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(orderSn).andMemberIdEqualTo(userId).andStatusEqualTo(0);
            List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

            if (ordersList.isEmpty()) return "order serial number does not exist: " + orderSn;

            Orders orders = ordersList.get(0);

            String paymentToken = orders.getPaymentId();
            String paymentLink = PAYPAL_PAYMENT_LINK + paymentToken;

            return "redirect:" + paymentLink;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Orders> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId) {
        return generateOrderSn(orderParam.getPayType(), 1) // TODO: add source type from request, web or mobile
                .flatMap(orderSn ->
                    Mono.fromCallable(() -> internalGenerateOrder(orderSn, orderParam, successUrl, cancelUrl, userId))
                            .subscribeOn(jdbcScheduler)
                );
    }

    private Orders internalGenerateOrder(String orderSn, OrderParam orderParam, String successUrl, String cancelUrl, int userId) {
        String couponCode = orderParam.getCoupon();
        AddressDTO address = orderParam.getAddress();
        int payType = orderParam.getPayType();  // TODO: make a payType enum and change postman

        Orders newOrder = new Orders();
        newOrder.setPayType(payType);
        newOrder.setSourceType(1);  // TODO: add source type from request, web or mobile

        List<OrderItem> orderItemList = new ArrayList<>();

        Map<String, Integer> skuQuantity = orderParam.getOrderProductSku(); // sku and quantity for order

        BigDecimal orderTotal = BigDecimal.ZERO;
        BigDecimal couponDiscount = orderParam.getDiscountAmount();

        // verify the price again
        for (String skuCode: skuQuantity.keySet()) {
            int quantity = orderParam.getOrderProductSku().get(skuCode);

            // find the sku and product
            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku sku = stockMapper.selectByExample(productSkuExample).get(0);

            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(sku.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductSkuId(sku.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductSkuCode(skuCode);
            orderItem.setProductName(product.getName());
            orderItem.setProductQuantity(quantity);
            orderItem.setRealAmount(sku.getPromotionPrice());

            orderTotal = sku.getPromotionPrice().multiply(BigDecimal.valueOf(quantity)).add(orderTotal);
            orderItemList.add(orderItem);
        }

        if (!hasStock(orderItemList)) {
            throw new OrderException("Not enough stock, Unable to order");
        }

        orderTotal = orderTotal.setScale(2, RoundingMode.HALF_UP);

        // verify price difference
        if (orderTotal.compareTo(orderParam.getAmount()) != 0) {
            throw new OrderException("Pricing error");
        }

        lockStock(orderItemList);

        // synchronous call to coupon service, assume the accepted discount amount is incorrect.
        BigDecimal checkedDiscount = checkCouponDiscountFromSms(couponCode, userId);
        couponDiscount = couponDiscount.compareTo(checkedDiscount) == 0 ? couponDiscount : BigDecimal.ZERO;

        // TODO: calculate shipping cost from UPS API, currently just fixed rate
        // Check shipping cost, $50 or more gets free shipping; otherwise, add $15
        if (orderTotal.compareTo(BigDecimal.valueOf(50)) < 0) {
            orderTotal = orderTotal.add(SHIPPING_COST);
        }

        orderTotal = orderTotal.subtract(couponDiscount);

        //newOrder.setPayType(payType);  // TODO: change data type to string in database

        newOrder.setOrderSn(orderSn);
        newOrder.setStatus(0);   // waiting for payment
        newOrder.setTotalAmount(orderTotal);
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

        if (!couponCode.isEmpty()) sendCouponUpdateMessage("coupon-out-0", new SmsCouponOutEvent(UPDATE_COUPON_USAGE, couponCode, userId, newOrderId));

        // clear cart
        cartItemService.clearCartItem(userId).subscribe();

        // scheduled order to be cancel if order not being paid with in time.
        sendOrderCancelMessage("orderCancelTTL-out-0", new OmsCompletionEvent(PAYMENT_FAILURE, orderSn));

        updateChangeLog(newOrderId, 0, "generate order","user");

        try {
            Payment payment = paypalService.createPayment(orderTotal, "USD", PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale, "payment description", cancelUrl, successUrl, orderSn);
            for(Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    String paymentURL = links.getHref();
                    String searchTerm = "token=";
                    int index = paymentURL.indexOf(searchTerm);
                    String paymentToken = paymentURL.substring(index + searchTerm.length());

                    // store payment token for later payment option
                    newOrder.setPaymentId(paymentToken);
                    ordersMapper.updateByPrimaryKeySelective(newOrder);

                    System.out.println("redirect:" + links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            LOG.error(e.getMessage());
        }

         return newOrder;
    }

    private BigDecimal checkCouponDiscountFromSms(String couponCode, int userId) {
        String url = SMS_SERVICE_URL + "/check?couponCode=" + couponCode;
        LOG.debug("Will call the list API on URL: {}", url);

        // Define the timeout in milliseconds
        int timeoutMilliseconds = 200; // 0.2 second

        BigDecimal discountAmount = BigDecimal.ZERO;

        // TODO: change it to use big decimal and non-blocking
        // Make a synchronous HTTP GET request with a timeout to the coupon service
//        Mono<Double> discountMono = webClient.get()
//                .uri(url)
//                .header("X-UserId", String.valueOf(userId))
//                .retrieve()
//                .onStatus(httpStatus -> httpStatus.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("Server error")))
//                .bodyToMono(Double.class)
//                .timeout(Duration.ofMillis(timeoutMilliseconds));
//
//        discountAmount = discountMono.blockOptional().orElse(0.0);

        return discountAmount;
    }

    @Override
    public Mono<Orders> paySuccess(String paymentId, String payerId) {
        return Mono.fromCallable(() -> {
            Orders newOrder = internalPaySuccess(paymentId, payerId);
            return newOrder;
        }).subscribeOn(jdbcScheduler);
    }

    private Orders internalPaySuccess(String paymentId, String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if (payment.getState().equals("approved")) {
                System.out.println("successful payment");

                Transaction transaction = payment.getTransactions().get(0);
                String saleId = transaction.getRelatedResources().get(0).getSale().getId();
                String orderSn = transaction.getCustom();

                // find and update the order, status, payment info
                OrdersExample ordersExample = new OrdersExample();
                ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
                Orders foundOrder = ordersMapper.selectByExample(ordersExample).get(0);

                foundOrder.setStatus(1);  // waiting for payment 0 , fulfilling 1,
                foundOrder.setPaymentId(saleId);
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
                    int quantityOrdered = item.getProductQuantity();

                    // find product and get current stock and update it
                    ProductExample productExample = new ProductExample();
                    productExample.createCriteria().andIdEqualTo(item.getProductId());
                    Product product = productMapper.selectByExample(productExample).get(0);

                    int currentStock = product.getStock();
                    product.setStock(currentStock - quantityOrdered);
                    productMapper.updateByPrimaryKey(product);

                    // update sku stock and free lock stock
                    ProductSkuExample skuExample = new ProductSkuExample();
                    skuExample.createCriteria().andSkuCodeEqualTo(item.getProductSkuCode()).andProductIdEqualTo(product.getId());
                    ProductSku productSku = stockMapper.selectByExample(skuExample).get(0);

                    currentStock = productSku.getStock();
                    productSku.setStock(currentStock - quantityOrdered);
                    productSku.setLockStock(productSku.getLockStock() - quantityOrdered);
                    stockMapper.updateByPrimaryKey(productSku);
                }
                // Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock
                sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));
                sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));

                OrderDetail newOrderDetail = orderDao.getOrderForNotification(orderSn);
                sendOrderEmailMessage("orderMessage-out-0", new OmsOrderEvent(ORDER_NEW, newOrderDetail));

                updateChangeLog(orderId, 1, "payment success","user");

                return foundOrder;
            }
        } catch (PayPalRESTException e) {
            LOG.error(e.getMessage());
        }
        // TODO: will cause a loop when try to pay more than once
        throw new OrderException("Error payment after success URL");
    }

    @Override
    public Mono<Void> delayedCancelOrder(String orderSn) {
        return Mono.fromRunnable(() -> {
            internalDelayedCancelOrder(orderSn);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void internalDelayedCancelOrder(String orderSn) {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        if (ordersList.isEmpty()) throw new RuntimeException("order serial number does not exist: " + orderSn);

        Orders newOrder = ordersList.get(0);

        // Order being paid, waiting for payment 0 , fulfilling(paid) 1
        if (newOrder.getStatus() == 1) return;

        newOrder.setUpdatedAt(new Date());
        newOrder.setStatus(5);
        ordersMapper.updateByPrimaryKeySelective(newOrder);

        int orderId = newOrder.getId();

        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderSnEqualTo(orderSn).andOrderIdEqualTo(orderId);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

        Map<String, Integer> itemOrderQuantity = new HashMap<>();

        // free up locked stock on local OMS
        for (OrderItem item : orderItemList) {
            int productId = item.getProductId();
            String skuCode = item.getProductSkuCode();
            int quantityNeeded = item.getProductQuantity();

            ProductSkuExample example = new ProductSkuExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSku sku = stockMapper.selectByExample(example).get(0);

            int itemStockLockNow = sku.getLockStock();
            sku.setLockStock(itemStockLockNow - quantityNeeded);
            stockMapper.updateByPrimaryKey(sku);

            itemOrderQuantity.put(skuCode, quantityNeeded);
        }

        // send stock update to PMS and SMS
        sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));

        OrderDetail cancelledOrder = orderDao.getOrderForNotification(orderSn);
        sendOrderEmailMessage("orderMessage-out-0", new OmsOrderEvent(ORDER_CANCEL, cancelledOrder));

        updateChangeLog(orderId, 5, "payment fail","user");
    }

    private void sendOrderCancelMessage(String bindingName, OmsCompletionEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName + " " + event.getEventCreatedAt());
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    @Override
    public Mono<Orders> cancelOrder(String orderSn) {
        return Mono.fromCallable(() -> {
            Orders cancelledOrder = internalCancelOrder(orderSn);
            return cancelledOrder;
        }).subscribeOn(jdbcScheduler);
    }

    private Orders internalCancelOrder(String orderSn) {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        // TODO: create OrderNotFoundException
        if (ordersList.isEmpty()) throw new RuntimeException("order not found with order serial number: " + orderSn);

        Orders foundOrder = ordersList.get(0);
        int orderId = foundOrder.getId();

        //  waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 , invalid 5
        int currentStatus = foundOrder.getStatus();

        if (currentStatus >= 2) throw new OrderException("Order already send out, can not cancel");

        // TODO :might add something like need admin approval after certain hours to cancel order
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

        Map<String, Integer> skuQuantity = new HashMap<>();

        // record order sku and quantity, and update OMS stock. free up stock.
        for (OrderItem orderItem : orderItemList) {
            String skuCode = orderItem.getProductSkuCode();
            int quantity = orderItem.getProductQuantity();
            skuQuantity.put(skuCode, quantity);

            // update OMS stock, increase product and sku stock. same as return.
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku sku = stockMapper.selectByExample(skuExample).get(0);

            // update sku stock
            int currentStock = sku.getStock();
            sku.setStock(currentStock + quantity);
            stockMapper.updateByPrimaryKeySelective(sku);

            // update stock product
            int productId = orderItem.getProductId();
            Product product = productMapper.selectByPrimaryKey(productId);
            currentStock = product.getStock();
            product.setStock(currentStock + quantity);
            productMapper.updateByPrimaryKeySelective(product);
        }

        foundOrder.setStatus(5);
        ordersMapper.updateByPrimaryKeySelective(foundOrder);

        sendProductStockUpdateMessage("product-out-0", new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));

        OrderDetail cancelledOrder = orderDao.getOrderForNotification(orderSn);
        sendOrderEmailMessage("orderMessage-out-0", new OmsOrderEvent(ORDER_CANCEL, cancelledOrder));

        updateChangeLog(orderId, 5, "Cancel order","user");

        return foundOrder;
    }

    @Override
    public void confirmReceiveOrder(String orderSn) {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        if (ordersList.isEmpty()) return;

        Orders order = ordersList.get(0);

        order.setStatus(3);
        order.setUpdatedAt(new Date());
        order.setReceiveTime(new Date());

        ordersMapper.updateByPrimaryKeySelective(order);

        OrderDetail deliveredOrder = orderDao.getOrderForNotification(orderSn);
        sendOrderEmailMessage("orderMessage-out-0", new OmsOrderEvent(ORDER_UPDATE, deliveredOrder));

        updateChangeLog(order.getId(), 3, "System check delivery status", "System");
    }

    // generate id with redis: date + source type + pay type + today's order % 6
    private Mono<String> generateOrderSn(int payType, int sourceType) {
        // Generate the key for Redis
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ORDER_ID + date;

        // Increment the Redis value and process the result reactively
        return redisService.incr(key, 1)
                .map(increment -> {
                    // Create the order SN
                    StringBuilder sb = new StringBuilder();
                    sb.append(date)
                            .append(increment);

                    sb.append(String.format("%02d", payType));
                    sb.append(String.format("%02d", sourceType));

                    String incrementStr = increment.toString();
                    if (incrementStr.length() <= 6) {
                        sb.append(String.format("%06d", increment));
                    } else {
                        sb.append(incrementStr);
                    }

                    return sb.toString();
                });
    }

    private void updateChangeLog(int orderId, int status, String note, String operator) {
        OrderChangeHistory changeLog = new OrderChangeHistory();
        changeLog.setOrderId(orderId);
        changeLog.setUpdateAction(note);
        changeLog.setOrderStatus(status);
        changeLog.setOperator(operator);
        changeLog.setNote(note);
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

            if (itemStock != null && itemStock.getStock() < (itemStock.getLockStock() + quantityNeeded)) {
                return false; // send alert somewhere here to alert admin about low stock
            }
        }
        return true;
    }

    private void sendProductStockUpdateMessage(String bindingName, PmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendCouponUpdateMessage(String bindingName, SmsCouponOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendOrderEmailMessage(String bindingName, OmsOrderEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    @Override
    public List<Orders> listAllSendingOrder() {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andStatusEqualTo(2);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);
        return ordersList;
    }

    @Override
    public List<OrderDetail> getUserPurchasedItem(String productSku) {
        return orderDao.getUserPurchasedItem(productSku);
    }
}
