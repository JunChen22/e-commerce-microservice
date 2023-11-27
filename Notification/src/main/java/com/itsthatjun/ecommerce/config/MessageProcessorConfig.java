package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.event.OmsOrderEvent;
import com.itsthatjun.ecommerce.dto.event.SmsEvent;
import com.itsthatjun.ecommerce.dto.event.UmsEmailEvent;
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

    @Autowired
    public MessageProcessorConfig(UmsService umsService) {
        this.umsService = umsService;
    }

    @Bean
    public Consumer<OmsOrderEvent> orderMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {
                case ORDER:
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_STATUS and DELETE_ACCOUNT event";
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
                case ORDER:
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_STATUS and DELETE_ACCOUNT event";
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
