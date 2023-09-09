package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.OmsCartEvent;
import com.itsthatjun.ecommerce.dto.event.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.OmsReturnOrderEvent;
import com.itsthatjun.ecommerce.dto.event.OmsOrderEvent;
import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReason;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import com.itsthatjun.ecommerce.service.CartItemService;
import com.itsthatjun.ecommerce.service.OrderService;
import com.itsthatjun.ecommerce.service.ReturnOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CartItemService cartItemService;

    private final OrderService orderService;

    private final ReturnOrderService returnOrderService;

    @Autowired
    public MessageProcessorConfig(CartItemService cartItemService, OrderService orderService, ReturnOrderService returnOrderService) {
        this.cartItemService = cartItemService;
        this.orderService = orderService;
        this.returnOrderService = returnOrderService;
    }

    @Bean
    public Consumer<OmsCartEvent<Integer, CartItem>> cartMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            int userId = event.getKey();
            int cartItemId = event.getCartItemId();
            List<CartItem> cartItems = event.getData();

            switch (event.getEventType()) {
                case ADD_ONE:
                    CartItem cartItem = cartItems.get(0);
                    cartItemService.addItem(cartItem, userId);
                    break;

                case ADD_ALL:
                    cartItemService.addAllItem(cartItems, userId);
                    break;

                case UPDATE:
                    int quantity = event.getQuantity();
                    cartItemService.updateQuantity(cartItemId, quantity, userId);
                    break;

                case DELETE:
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
    public Consumer<OmsOrderEvent> orderMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case GENERATE_ORDER:
                    System.out.println("at generating order at the consumer");
                    orderService.generateOrder(event.getOrderParam(), event.getSuccessUrl(), event.getCancelUrl(), event.getUserId());
                    break;

                case CANCEL_ORDER:
                    orderService.cancelOrder("TODO");
                    break;

                case UPDATE_ORDER:
                    orderService.update();
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
    public Consumer<OmsCompletionEvent> orderCompleteMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case PAYMENT_SUCCESS:
                    orderService.paySuccess(event.getOrderSN(), event.getPaymentId(), event.getPayerId());
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
    public Consumer<OmsReturnOrderEvent> returnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            String orderSn = event.getOrderSn();
            int userId = event.getUserId();
            OrderReturnApply returnApply = event.getReturnApply();
            OrderReturnReason returnReason = event.getReturnReason();
            List<OrderReturnReasonPictures> pictures = event.getPictures();

            switch (event.getEventType()) {
                case APPLY:
                    returnOrderService.applyForReturn(returnApply, pictures, orderSn, userId);
                    break;

                case UPDATE:
                    returnOrderService.updateReturn(returnApply, pictures, orderSn, userId);
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
}
