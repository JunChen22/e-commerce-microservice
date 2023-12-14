package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.event.*;
import com.itsthatjun.ecommerce.service.OmsService;
import com.itsthatjun.ecommerce.service.UmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final UmsService umsService;

    private final OmsService omsService;

    @Autowired
    public MessageProcessorConfig(UmsService umsService, OmsService omsService) {
        this.umsService = umsService;
        this.omsService = omsService;
    }

    @Bean
    public Consumer<OmsOrderEvent> orderMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            OrderDetail orderDetail = event.getOrderDetail();
            switch (event.getEventType()) {
                case ORDER_NEW:
                    omsService.newOrderMessage(orderDetail);
                    break;

                case ORDER_UPDATE:
                    omsService.updateOrderMessage(orderDetail);
                    break;

                case ORDER_CANCEL:
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected " +
                            "ORDER_NEW, ORDER_UPDATE and ORDER_ITEM_ANNOUNCEMENT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<OmsOrderAnnouncementEvent> orderItemMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            String productName = event.getProductName();;
            String message = event.getMessage();
            switch (event.getEventType()) {
                case ORDER_ITEM_SKU:
                    omsService.sendAllItemMessage(productName, message);
                    break;

                case ORDER_ITEM_PRODUCT:
                    //omsService.sendAllItemMessage(productName, message); TODO: might just do sku instead
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected " +
                            "ORDER_ITEM_SKU and ORDER_ITEM_PRODUCT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<OmsReturnEvent> orderReturnMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            ReturnDetail returnDetail = event.getReturnDetail();
            UserInfo userInfo = event.getUserInfo();
            switch (event.getEventType()) {
                case NEW_RETURN:
                    omsService.newReturnMessage(returnDetail, userInfo);
                    break;

                case RETURN_UPDATE:
                    omsService.updateReturnMessage(returnDetail, userInfo);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected " +
                            "NEW_RETURN and RETURN_UPDATE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<SmsEvent> saleMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case NEW_SALE:
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "NEW_SALE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<UmsEmailEvent> userMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventType());
            String message = event.getMessage();
            switch (event.getEventType()) {
                case ONE_USER:
                    UserInfo userInfo = event.getUserInfo();
                    umsService.sendUserMessage(userInfo, message);
                    break;

                case ALL_USER:
                    umsService.sendAllUserMessage(message);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected  ONE_USER" +
                            " and ALL_USER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<UmsEmailEvent> adminMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventType());
            String message = event.getMessage();
            switch (event.getEventType()) {
                case ONE_USER:
                    UserInfo userInfo = event.getUserInfo();
                    umsService.sendUserMessage(userInfo, message);
                    break;

                case ALL_USER:
                    umsService.sendAllUserMessage(message);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected  ONE_USER" +
                            " and ALL_USER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
