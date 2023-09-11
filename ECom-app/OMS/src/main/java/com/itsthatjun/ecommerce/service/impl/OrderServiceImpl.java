package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.config.PaypalPaymentIntent;
import com.itsthatjun.ecommerce.config.PaypalPaymentMethod;
import com.itsthatjun.ecommerce.dto.ConfirmOrderResult;
import com.itsthatjun.ecommerce.dto.event.PmsProductEvent;
import com.itsthatjun.ecommerce.dto.event.SmsCouponEvent;
import com.itsthatjun.ecommerce.dto.event.SmsSalesStockEvent;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.exceptions.OrderException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.OrderService;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.itsthatjun.ecommerce.dto.event.PmsProductEvent.Type.*;
import static com.itsthatjun.ecommerce.dto.event.SmsCouponEvent.Type.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final double SHIPPING_COST = 15;  // default shipping cost for order less than 50

    private final StreamBridge streamBridge;

    private PaypalService paypalService;

    private OrdersMapper ordersMapper;

    private CartItemServiceImpl cartItemService;

    private OrderItemMapper orderItemMapper;

    private ProductSkuStockMapper stockMapper;

    private ProductMapper productMapper;

    @Value("${app.SMS-service.host}")
    String couponServiceURL;
    @Value("${app.SMS-service.port}")
    int smsPort;

    @Autowired
    public OrderServiceImpl(StreamBridge streamBridge, PaypalService paypalService, OrdersMapper ordersMapper,
                            CartItemServiceImpl cartItemService, OrderItemMapper orderItemMapper, ProductSkuStockMapper stockMapper,
                            ProductMapper productMapper) {
        this.streamBridge = streamBridge;
        this.paypalService = paypalService;
        this.ordersMapper = ordersMapper;
        this.cartItemService = cartItemService;
        this.orderItemMapper = orderItemMapper;
        this.stockMapper = stockMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Mono<Orders> detail(String orderSN) {
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSN);
        if (ordersMapper.selectByExample(ordersExample).isEmpty())
            return Mono.empty();
        return Mono.fromCallable(() -> ordersMapper.selectByExample(ordersExample).get(0));
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
    public Mono<ConfirmOrderResult> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId) {

        String orderSn = generateOrderSn();
        String couponCode = orderParam.getCoupon();

        Orders newOrder = new Orders();
        List<OrderItem> orderItemList = new ArrayList<>();

        Map<String, Integer> skuQuantity = orderParam.getOrderProductSku(); // sku and quantity for order

        double orderTotal = 0;
        double couponDiscount = orderParam.getDiscountAmount();

        // verify the price again
        for (String productSku: skuQuantity.keySet()) {

            int quantity = orderParam.getOrderProductSku().get(productSku);

            // find the sku and product
            ProductSkuStockExample productSkuExample = new ProductSkuStockExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(productSku);
            ProductSkuStock productSkuStock = stockMapper.selectByExample(productSkuExample).get(0);

            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(productSkuStock.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductQuantity(orderParam.getOrderProductSku().get(productSku));
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

        // TODO: it's synchronous call to coupon service, assume the accepted discount amount is correct.
        // couponDiscount = couponDiscount == couponservice.checkcoupon(coupon)? couponDiscount : 0;

        // check shipping cost, $50 or more get free shipping or $15
        // TODO: calculate shipping cost from UPS API, currently just fixed rate
        if (orderTotal < 50) orderTotal += SHIPPING_COST;

        orderTotal = orderTotal - couponDiscount;

        newOrder.setOrderSn(orderSn);
        newOrder.setStatus(0);   // waiting for payment
        newOrder.setTotalAmount(BigDecimal.valueOf(orderTotal));
        ordersMapper.insert(newOrder);

        int newOrderId = newOrder.getId();

        for (OrderItem item : orderItemList) {
            item.setOrderSn(orderSn);
            item.setOrderId(newOrderId);
            orderItemMapper.insert(item);
        }

        // Generated order, increase sku lock stock
        sendProductStockUpdateMessage("product-out-0", new PmsProductEvent(PmsProductEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockEvent(SmsSalesStockEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));

        if (!couponCode.equals("")) { // update coupon usage, won't go up even return or payment fail
            sendCouponUpdateMessage("coupon-out-0", new SmsCouponEvent(UPDATE_COUPON_USAGE, couponCode, userId, newOrderId));
        }

        // clear cart
        cartItemService.clearCartItem(userId);

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

        return Mono.empty();
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
                    ProductSkuStockExample productSkuStockExample = new ProductSkuStockExample();
                    productSkuStockExample.createCriteria().andSkuCodeEqualTo(item.getProductSkuCode()).andProductIdEqualTo(product.getId());
                    ProductSkuStock producutSku = stockMapper.selectByExample(productSkuStockExample).get(0);

                    currentStock = producutSku.getStock();
                    producutSku.setStock(currentStock - quantityOrdered);
                    producutSku.setLockStock(producutSku.getLockStock() - quantityOrdered);
                    stockMapper.updateByPrimaryKey(producutSku);
                }
                // Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock
                sendProductStockUpdateMessage("product-out-0",new PmsProductEvent(PmsProductEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));
                sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockEvent(SmsSalesStockEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, itemOrderQuantity));

                return Mono.just(foundOrder);
            }
        } catch (PayPalRESTException e) {
            LOG.error(e.getMessage());
        }
        return Mono.empty();
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

            ProductSkuStockExample example = new ProductSkuStockExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = stockMapper.selectByExample(example).get(0);

            int itemStockLockNow = skuStock.getLockStock();
            skuStock.setLockStock(itemStockLockNow - quantityNeeded);
            stockMapper.updateByPrimaryKey(skuStock);

            itemOrderQuantity.put(skuCode, quantityNeeded);
        }
        // Generated order and failure payment, decrease sku lock stock
        sendProductStockUpdateMessage("product-out-0",new PmsProductEvent(PmsProductEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockEvent(SmsSalesStockEvent.Type.UPDATE_FAIL_PAYMENT, orderSn, itemOrderQuantity));
    }

    @Override
    @ApiOperation("update order status, ")
    public Mono<Orders> update() {
        return Mono.empty();
    }

    @Bean
    @ApiOperation("comsume message to update stock")
    public Mono<Orders> updateStock() {
        return Mono.empty();
    }


    @Override
    public String cancelOrder(String orderSn) {

        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        Orders foundOrder = ordersMapper.selectByExample(ordersExample).get(0);

        if (foundOrder.getOrderSn() == null) {
            throw new RuntimeException("order not found with order serial number: " + orderSn); // TODO: create OrderNotFoundException
        }

        //  waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 , invalid 5
        int currentStatus = foundOrder.getStatus();

        if (currentStatus >= 2) {
            return "Order already send out, can not cancel";
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
            ProductSkuStockExample skuStockExample = new ProductSkuStockExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = stockMapper.selectByExample(skuStockExample).get(0);

            int currentStock = skuStock.getStock();

            skuStock.setStock(currentStock + quantity);
            stockMapper.updateByPrimaryKey(skuStock);

        }

        sendProductStockUpdateMessage("product-out-0", new PmsProductEvent(PmsProductEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockEvent(SmsSalesStockEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));

        foundOrder.setStatus(5);

        ordersMapper.updateByPrimaryKeySelective(foundOrder);

        return "order cancelled: " + orderSn;
    }

    // TODO: use a better way to generate order serial number
    private String generateOrderSn() {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
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

    private void lockStock(List<OrderItem> orderItemList) {

        for (OrderItem item : orderItemList) {
            int productId = item.getProductId();
            String skuCode = item.getProductSkuCode();
            int quantityNeeded = item.getProductQuantity();

            ProductSkuStockExample example = new ProductSkuStockExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSkuStock itemStock = stockMapper.selectByExample(example).get(0);

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
            ProductSkuStockExample example = new ProductSkuStockExample();
            example.createCriteria().andProductIdEqualTo(productId).andSkuCodeEqualTo(skuCode);
            ProductSkuStock itemStock = stockMapper.selectByExample(example).get(0);

            if (itemStock != null && itemStock.getStock() < (itemStock.getLockStock() + quantityNeeded)){
                return false; // send alert somewhere here to alert admin about low stock
            }
        }
        return true;
    }

    private void sendProductStockUpdateMessage(String bindingName, PmsProductEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendCouponUpdateMessage(String bindingName, SmsCouponEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderId())
                .build();
        streamBridge.send(bindingName, message);
    }
}
