package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.SmsEvent;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.SmsEvent.Type.UPDATE_COUPON_USAGE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CouponServiceImpl couponService;

    @Autowired
    public MessageProcessorConfig(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @Bean
    public Consumer<SmsEvent> couponMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            if(event.getEventType() == UPDATE_COUPON_USAGE) {
                System.out.println("at getting message a sms");
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ",=======================================";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
