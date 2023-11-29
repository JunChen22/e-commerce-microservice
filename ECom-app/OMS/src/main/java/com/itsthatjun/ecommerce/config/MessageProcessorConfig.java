package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.event.admin.OmsAdminOrderReturnEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.CartItemService;
import com.itsthatjun.ecommerce.service.OrderService;
import com.itsthatjun.ecommerce.service.ReturnOrderService;
import com.itsthatjun.ecommerce.service.admin.AdminOrderServiceImpl;
import com.itsthatjun.ecommerce.service.admin.AdminReturnServiceImpl;
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

    private final AdminOrderServiceImpl adminOrderService;

    private final AdminReturnServiceImpl adminReturnService;

    @Autowired
    public MessageProcessorConfig(CartItemService cartItemService, OrderService orderService, ReturnOrderService returnOrderService,
                                  PmsEventUpdateService pmsEventUpdateService, SmsEventUpdateService smsEventUpdateService,
                                  AdminOrderServiceImpl adminOrderService, AdminReturnServiceImpl adminReturnService) {
        this.cartItemService = cartItemService;
        this.orderService = orderService;
        this.returnOrderService = returnOrderService;
        this.pmsEventUpdateService = pmsEventUpdateService;
        this.smsEventUpdateService = smsEventUpdateService;
        this.adminOrderService = adminOrderService;
        this.adminReturnService = adminReturnService;
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
                    cartItemService.addItem(cartItem, userId).subscribe();
                    break;

                case UPDATE:
                    int quantity = cartItem.getQuantity();
                    cartItemId = cartItem.getCartId();
                    cartItemService.updateQuantity(cartItemId, quantity, userId).subscribe();
                    break;

                case DELETE:
                    cartItemId = cartItem.getCartId();
                    cartItemService.deleteCartItem(cartItemId, userId).subscribe();
                    break;

                case CLEAR:
                    cartItemService.clearCartItem(userId).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected ADD_ONE, ADD_ALL, " +
                            "UPDATE, DELETE, and CLEAR event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<OmsReturnEvent> returnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            int userId = event.getUserId();

            ReturnParam returnParam = event.getReturnParam();
            ReturnRequest returnRequest = returnParam.getReturnRequest();
            List<ReturnReasonPictures> pictures = returnParam.getPicturesList();
            String orderSn = returnParam.getReturnRequest().getOrderSn();

            switch (event.getEventType()) {
                case APPLY:
                    Map<String, Integer> skuQuantity = returnParam.getSkuQuantity();
                    returnOrderService.applyForReturn(returnRequest, pictures, skuQuantity,userId).subscribe();
                    break;

                case UPDATE:
                    returnOrderService.updateReturnInfo(returnRequest, pictures, orderSn, userId).subscribe();
                    break;

                case CANCEL:
                    returnOrderService.cancelReturn(orderSn, userId).subscribe();
                    break;

                case REJECT:
                    returnOrderService.delayedRejectReturn(orderSn, userId).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected APPLY, UPDATE, CANCEL and REJECT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
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
                    orderService.generateOrder(event.getOrderParam(), event.getSuccessUrl(), event.getCancelUrl(), event.getUserId()).subscribe();
                    break;

                case CANCEL_ORDER:
                    String orderSn = event.getOrderSn();
                    orderService.cancelOrder(orderSn).subscribe();
                    break;

                // case UPDATE_ORDER:  TODO: update order minutes after order placed, like change quantity or cancel partial order

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected GENERATE_ORDER, CANCEL_ORDER, " +
                            "and UPDATE_ORDER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
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
                    orderService.paySuccess(event.getPaymentId(), event.getPayerId()).subscribe();
                    break;

                case PAYMENT_FAILURE:
                    orderService.delayedCancelOrder(event.getOrderSn()).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected PAYMENT_SUCCESS and PAYMENT_FAILURE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    // TODO: fix the from DTO change, add generic MapStruct mapper to map the DTO models
    /*
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
                    Address address = orderDetail.getAddress();
                    adminOrderService.createOrder(order, orderItemList, address, reason, operator).subscribe();
                    break;

                case UPDATE_ORDER:
                    adminOrderService.updateOrder(order, reason, operator).subscribe();
                    break;

                case CANCEL_ORDER:
                    adminOrderService.adminCancelOrder(order, reason, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected GENERATE_ORDER, CANCEL_ORDER, " +
                            "and UPDATE_ORDER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
   */

    @Bean
    public Consumer<OmsAdminOrderReturnEvent> adminReturnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            String operator = event.getAdminName();
            ReturnRequestDecision returnRequestDecision = event.getReturnRequestDecision();

            switch (event.getEventType()) {
                case APPROVED:
                    adminReturnService.approveReturnRequest(returnRequestDecision, operator).subscribe();
                    break;

                case REJECTED:
                    String rejectionReason = returnRequestDecision.getReason();
                    adminReturnService.rejectReturnRequest(returnRequestDecision, rejectionReason, operator).subscribe();
                    break;

                case COMPLETED_RETURN:
                    adminReturnService.completeReturnRequest(returnRequestDecision, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected APPLY, UPDATE and CANCEL event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    /* // TODO:
    @Bean
    public Consumer<OmsAdminOrderAnnouncementEvent> adminOrderItemAnnouncementProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            String operator = event.getAdminName();
            ReturnRequestDecision returnRequestDecision = event.getReturnRequestDecision();

            switch (event.getEventType()) {
                case APPROVED:
                    adminReturnService.approveReturnRequest(returnRequestDecision, operator).subscribe();
                    break;

                case REJECTED:
                    String rejectionReason = returnRequestDecision.getReason();
                    adminReturnService.rejectReturnRequest(returnRequestDecision, rejectionReason, operator).subscribe();
                    break;

                case COMPLETED_RETURN:
                    adminReturnService.completeReturnRequest(returnRequestDecision, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected APPLY, UPDATE and CANCEL event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
     */

    @Bean
    public Consumer<PmsUpdateIncomingEvent> updateFromPmsMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Product newProduct = event.getProduct();
            List<ProductSku> productSkuList = event.getProductSkuList();

            switch (event.getEventType()) {
                case NEW_PRODUCT:
                    pmsEventUpdateService.addProduct(newProduct, productSkuList).subscribe();
                    break;

                case NEW_PRODUCT_SKU:
                    ProductSku newSku = productSkuList.get(0);
                    pmsEventUpdateService.addProductSku(newSku).subscribe();
                    break;

                case UPDATE_PRODUCT:
                    pmsEventUpdateService.updateProduct(newProduct, productSkuList).subscribe();
                    break;

                case REMOVE_PRODUCT_SKU:
                    ProductSku removeSku = productSkuList.get(0);
                    pmsEventUpdateService.removeProductSku(removeSku).subscribe();
                    break;

                case REMOVE_PRODUCT:
                    pmsEventUpdateService.removeProduct(newProduct, productSkuList).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_PRODUCT, NEW_PRODUCT_SKU, UPDATE_PRODUCT, " +
                            " REMOVE_PRODUCT_SKU and REMOVE_PRODUCT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
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
                smsEventUpdateService.updateSale(skuList).subscribe();
            } else if (event.getEventType() == REMOVE_SALE) {
                smsEventUpdateService.removeSale(skuList).subscribe();
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_SALE_PRICE" +
                            " event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        };
    }
}
