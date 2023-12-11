package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.config.PaypalPaymentIntent;
import com.itsthatjun.ecommerce.config.PaypalPaymentMethod;
import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsOrderAnnouncementEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsSalesStockOutEvent;
import com.itsthatjun.ecommerce.exceptions.OrderException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.AdminOrderService;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.itsthatjun.ecommerce.service.impl.OrderServiceImpl;
import com.itsthatjun.ecommerce.service.impl.RedisServiceImpl;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itsthatjun.ecommerce.dto.event.outgoing.OmsOrderAnnouncementEvent.Type.ORDER_ITEM_PRODUCT;
import static com.itsthatjun.ecommerce.dto.event.outgoing.OmsOrderAnnouncementEvent.Type.ORDER_ITEM_SKU;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PaypalService paypalService;

    private final RedisServiceImpl redisService;

    private final OrdersMapper ordersMapper;

    private final OrderItemMapper orderItemMapper;

    private final ProductSkuMapper stockMapper;

    private final ProductMapper productMapper;

    private final OrderChangeHistoryMapper changeHistoryMapper;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final String PAYPAL_PAYMENT_LINK = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";

    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    private final WebClient webClient;

    @Autowired
    public AdminOrderServiceImpl(PaypalService paypalService, RedisServiceImpl redisService, OrdersMapper ordersMapper,
                             OrderItemMapper orderItemMapper, ProductSkuMapper stockMapper, ProductMapper productMapper,
                                 OrderChangeHistoryMapper changeHistoryMapper, StreamBridge streamBridge, WebClient.Builder webClient,
                                 @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.paypalService = paypalService;
        this.redisService = redisService;
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.stockMapper = stockMapper;
        this.productMapper = productMapper;
        this.changeHistoryMapper = changeHistoryMapper;
        this.streamBridge = streamBridge;
        this.webClient = webClient.build();
        this.jdbcScheduler = jdbcScheduler;
    }

    // TODO: change these based of status code and add 4 and 5 in
    // waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5
    @Override
    public Flux<Orders> getAllOrderByStatus(int statusCode) {
        return Mono.fromCallable(() -> {
            OrdersExample example = new OrdersExample();
            example.createCriteria().andStatusEqualTo(statusCode);
            List<Orders> orderWaitingForPayment = ordersMapper.selectByExample(example);
            return orderWaitingForPayment;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<OrderDetail> getUserOrderDetail(String orderSn) {
        return Mono.fromCallable(() -> {
            // TODO: fix dao
            //OrderDetail orderDetail = orderDao.getDetail(orderSn);

            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
            List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);
            if (ordersList.isEmpty()) throw new RuntimeException("Order serial number does not exist: " + orderSn);

            Orders order = ordersList.get(0);

            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderSnEqualTo(orderSn);
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

            OrderDetail result = new OrderDetail();
            /* TODO: fix
            result.setOrders(order);
            result.setOrderItemList(orderItemList);
             */
            return result;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Orders> getUserOrders(int memberId) {
        return Mono.fromCallable(() -> {
            OrdersExample example = new OrdersExample();
            example.createCriteria().andMemberIdEqualTo(memberId);
            List<Orders> ordersList = ordersMapper.selectByExample(example);
            return ordersList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<String> getUserOrderPaymentLink(String orderSn) {
        return Mono.fromCallable(() -> {
            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(orderSn).andStatusEqualTo(0);
            List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

            if (ordersList.isEmpty()) return "order serial number does not exist: " + orderSn;

            Orders orders = ordersList.get(0);

            String paymentToken = orders.getPaymentId();
            String paymentLink = PAYPAL_PAYMENT_LINK + paymentToken;

            return "redirect:" + paymentLink;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Orders> createOrder(Orders order, List<OrderItem> orderItemList, Address address, String reason, String operator) {
        return Mono.fromCallable(() -> {
            Orders newOrder = internalCreateOrder(order, orderItemList, address, reason, operator);
            return newOrder;
        }).subscribeOn(jdbcScheduler);
    }

    private Orders internalCreateOrder(Orders newOrder, List<OrderItem> orderItemList, Address address, String reason, String operator) {
        String orderSn = generateOrderSn(newOrder);
        newOrder.setOrderSn(orderSn);
        newOrder.setCreatedAt(new Date());
        newOrder.setAdminNote(reason);

        double orderTotal = 0;

        // set address
        newOrder.setReceiverName(address.getReceiverName());
        newOrder.setReceiverPhone(address.getPhoneNumber());
        newOrder.setReceiverDetailAddress(address.getDetailAddress());
        newOrder.setReceiverCity(address.getCity());
        newOrder.setReceiverState(address.getState());
        newOrder.setReceiverZipCode(address.getZipCode());
        newOrder.setComment(address.getNote());  // special instruction like leave the package between the door

        // fill in order item info
        for (OrderItem item : orderItemList) {
            int quantity = item.getProductQuantity();
            String productSku = item.getProductSkuCode();

            // find the sku and product
            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(productSku);
            ProductSku sku = stockMapper.selectByExample(productSkuExample).get(0);

            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(sku.getProductId());
            Product product = productMapper.selectByExample(productExample).get(0);

            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductQuantity(quantity);
            item.setPromotionAmount(sku.getPromotionPrice());
            item.setProductSkuCode(productSku);

            orderTotal += sku.getPromotionPrice().doubleValue() * quantity;
        }

        if (!hasStock(orderItemList)) throw new OrderException("Not enough stock, Unable to order");

        lockStock(orderItemList);

        ordersMapper.insert(newOrder);
        int orderId = newOrder.getId();

        // insert order item and prep for other service stock update
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

        if (newOrder.getStatus() == 0) { // waiting for payment
            try {
                Payment payment = paypalService.createPayment(orderTotal, "USD", PaypalPaymentMethod.paypal,
                        PaypalPaymentIntent.sale, "payment description",   "/", "/", orderSn);
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
        } else { // free replacement parts/warranty
            // free up stock
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
                ProductSku producutSku = stockMapper.selectByExample(skuExample).get(0);

                currentStock = producutSku.getStock();
                producutSku.setStock(currentStock - quantityOrdered);
                producutSku.setLockStock(producutSku.getLockStock() - quantityOrdered);
                stockMapper.updateByPrimaryKey(producutSku);
            }
            sendProductStockUpdateMessage("product-out-0",new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, skuQuantity));
            sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_PURCHASE_PAYMENT, orderSn, skuQuantity));
        }

        updateChangeLog(orderId, 1, reason,operator);
        return newOrder;
    }

    @Override
    public Mono<Orders> updateOrder(Orders updateOrder, String reason, String operator) {
        return Mono.fromCallable(() -> {
            String orderSn = updateOrder.getOrderSn();

            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
            List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

            if (ordersList.isEmpty()) throw new RuntimeException("Can not find order with serial number: " + orderSn);
            Orders order = ordersList.get(0);

            order.setUpdatedAt(new Date());
            order.setAdminNote(reason);

            ordersMapper.updateByPrimaryKeySelective(order);
            // TODO: find the find the order by serial number and check if i can update it before shipping out
            int orderId = updateOrder.getId();
            int status = updateOrder.getStatus();
            updateChangeLog(orderId, status, reason,operator);
            return updateOrder;

            // TODO: send update to other service if updates affect other service like adding product so need to update
            // stock on other services.
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> adminCancelOrder(Orders updateOrder, String reason, String operator) {
        return Mono.fromRunnable(() -> {
            String orderSn = updateOrder.getOrderSn();

            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
            List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

            if (ordersList.isEmpty()) throw new RuntimeException("Can not find order with serial number: " + orderSn);
            Orders order = ordersList.get(0);

            order.setComment(reason);
            order.setAdminNote(reason);
            order.setUpdatedAt(new Date());
            order.setStatus(4);
            ordersMapper.updateByPrimaryKeySelective(order);

            // TODO: find the find the order by serial number, and check if i can cancel it before shipping out
            int orderId = order.getId();
            updateChangeLog(orderId, 4, reason, operator);
        }).subscribeOn(jdbcScheduler).then();
    }

    // generate id with redis: date + source type + pay type + today's order % 6
    private String generateOrderSn(Orders newOrder) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);
        sb.append(date + increment);

        sb.append(String.format("%02d", newOrder.getSourceType()));
        sb.append(String.format("%02d", newOrder.getPayType()));
        String incrementStr = increment.toString();

        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    private void updateChangeLog(int orderId, int status, String note, String operator) {
        OrderChangeHistory changeLog = new OrderChangeHistory();
        changeLog.setOrderId(orderId);
        changeLog.setUpdateAction(note);
        changeLog.setOrderStatus(status);
        changeLog.setOperator(operator);
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

    @Override
    public Mono<Void> adminOrderItemAnnouncement(String productSku, String message, String operator) {
        return Mono.fromRunnable(() -> {
            OmsOrderAnnouncementEvent event = new OmsOrderAnnouncementEvent(ORDER_ITEM_SKU, message, operator);
            sendSalesAnnouncementMessage("orderItemMessage-out-0", event);
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Mono<Void> adminOrderProductAnnouncement(String productName, String message, String operator) {
        return Mono.fromRunnable(() -> {
            OmsOrderAnnouncementEvent event = new OmsOrderAnnouncementEvent(ORDER_ITEM_PRODUCT, message, operator);
            sendSalesAnnouncementMessage("orderItemMessage-out-0", event);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void sendSalesAnnouncementMessage(String bindingName, OmsOrderAnnouncementEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
