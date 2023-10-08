package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.event.admin.OmsAdminOrderEvent;
import com.itsthatjun.ecommerce.dto.event.admin.OmsAdminOrderReturnEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.*;
import com.itsthatjun.ecommerce.service.eventupdate.PmsEventUpdateService;
import com.itsthatjun.ecommerce.service.eventupdate.SmsEventUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent.Type.REMOVE_SALE;
import static com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent.Type.UPDATE_SALE_PRICE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CartItemService cartItemService;

    private final OrderService orderService;

    private final ReturnOrderService returnOrderService;

    private final PmsEventUpdateService pmsEventUpdateService;

    private final SmsEventUpdateService smsEventUpdateService;

    @Autowired
    public MessageProcessorConfig(CartItemService cartItemService, OrderService orderService, ReturnOrderService returnOrderService,
                                  PmsEventUpdateService pmsEventUpdateService, SmsEventUpdateService smsEventUpdateService) {
        this.cartItemService = cartItemService;
        this.orderService = orderService;
        this.returnOrderService = returnOrderService;
        this.pmsEventUpdateService = pmsEventUpdateService;
        this.smsEventUpdateService = smsEventUpdateService;
    }

    @Bean
    public Consumer<OmsCartEvent> cartMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            CartItem cartItem = event.getCartItem();
            int userId = event.getUserId();
            int cartItemId;

            switch (event.getEventType()) {
                case ADD_ONE:
                    cartItemService.addItem(cartItem, userId);
                    break;

                case UPDATE:
                    int quantity = cartItem.getQuantity();
                    cartItemId = cartItem.getCartId();
                    cartItemService.updateQuantity(cartItemId, quantity, userId);
                    break;

                case DELETE:
                    cartItemId = cartItem.getCartId();
                    cartItemService.deleteCartItem(cartItemId, userId);
                    break;

                case CLEAR:
                    cartItemService.clearCartItem(userId);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected ADD_ONE, ADD_ALL, " +
                            "UPDATE, DELETE, and CLEAR event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsReturnEvent> returnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            String orderSn = event.getReturnRequest().getOrderSn();
            int userId = event.getUserId();

            ReturnParam returnParam = event.getReturnParam();
            ReturnRequest returnRequest = returnParam.getReturnRequest();
            List<ReturnReasonPictures> pictures = returnParam.getPicturesList();

            switch (event.getEventType()) {
                case APPLY:
                    Map<String, Integer> skuQuantity = returnParam.getSkuQuantity();
                    returnOrderService.applyForReturn(returnRequest, pictures, skuQuantity,userId);
                    break;

                case UPDATE:
                    returnOrderService.updateReturnInfo(returnRequest, pictures, orderSn, userId);
                    break;

                case CANCEL:
                    returnOrderService.cancelReturn(orderSn, userId);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected APPLY, UPDATE and CANCEL event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsOrderEvent> orderMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case GENERATE_ORDER:
                    orderService.generateOrder(event.getOrderParam(), event.getSuccessUrl(), event.getCancelUrl(), event.getUserId());
                    break;

                case CANCEL_ORDER:
                    String orderSn = event.getOrderSn();
                    orderService.cancelOrder(orderSn);
                    break;

                // case UPDATE_ORDER:  TODO: update order minutes after order placed, like change quantity or cancel partial order

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected GENERATE_ORDER, CANCEL_ORDER, " +
                            "and UPDATE_ORDER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsCompletionEvent> orderCompleteMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case PAYMENT_SUCCESS:
                    orderService.paySuccess(event.getPaymentId(), event.getPayerId());
                    break;

                case PAYMENT_FAILURE:
                    orderService.payFail(event.getOrderSN());
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected PAYMENT_SUCCESS and PAYMENT_FAILURE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsAdminOrderEvent> adminOrderMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            OrderDetail orderDetail = event.getOrderDetail();
            Orders order = orderDetail.getOrders();

            String reason = event.getReason();
            String operator = event.getOperator();
            switch (event.getEventType()) {
                case GENERATE_ORDER:
                    List<OrderItem> orderItemList = orderDetail.getOrderItemList();
                    orderService.createOrder(order, orderItemList, reason, operator);
                    break;

                case UPDATE_ORDER:
                    orderService.updateOrder(order, reason, operator);
                    break;

                case CANCEL_ORDER:
                    orderService.adminCancelOrder(order, reason, operator);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected GENERATE_ORDER, CANCEL_ORDER, " +
                            "and UPDATE_ORDER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsAdminOrderReturnEvent> adminReturnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            String operator = event.getAdminName();
            ReturnRequestDecision returnRequestDecision = event.getReturnRequestDecision();

            switch (event.getEventType()) {
                case APPROVED:
                    returnOrderService.approveReturnRequest(returnRequestDecision, operator);
                    break;

                case REJECTED:
                    String rejectionReason = returnRequestDecision.getReason();
                    returnOrderService.rejectReturnRequest(returnRequestDecision, rejectionReason, operator);
                    break;

                case COMPLETED_RETURN:
                    returnOrderService.completeReturnRequest(returnRequestDecision, operator);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected APPLY, UPDATE and CANCEL event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<PmsUpdateIncomingEvent> updateFromPmsMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Product newProduct = event.getProduct();
            List<ProductSku> productSkuList = event.getProductSkuList();

            switch (event.getEventType()) {

                case NEW_PRODUCT:
                    pmsEventUpdateService.addProduct(newProduct, productSkuList);
                    break;

                case NEW_PRODUCT_SKU:
                    ProductSku newSku = productSkuList.get(0);
                    pmsEventUpdateService.addProductSku(newSku);
                    break;

                case UPDATE_PRODUCT:
                    pmsEventUpdateService.updateProduct(newProduct, productSkuList);
                    break;

                case REMOVE_PRODUCT_SKU:
                    ProductSku removeSku = productSkuList.get(0);
                    pmsEventUpdateService.removeProductSku(removeSku);
                    break;

                case REMOVE_PRODUCT:
                    pmsEventUpdateService.removeProduct(newProduct, productSkuList);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_PRODUCT, NEW_PRODUCT_SKU, UPDATE_PRODUCT, " +
                            " REMOVE_PRODUCT_SKU and REMOVE_PRODUCT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<SmsUpdateIncomingEvent> updateFromSmsMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            List<ProductSku> skuList = event.getSkuList();
            if (event.getEventType() == UPDATE_SALE_PRICE) {
                smsEventUpdateService.updateSale(skuList);
            } else if (event.getEventType() == REMOVE_SALE) {
                smsEventUpdateService.removeSale(skuList);
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_SALE_PRICE" +
                            " event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
