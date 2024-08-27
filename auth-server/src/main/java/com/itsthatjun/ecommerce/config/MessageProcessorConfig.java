package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.Incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.service.eventupdate.UmsEventUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final UmsEventUpdateService umsEventUpdateService;

    @Autowired
    public MessageProcessorConfig(UmsEventUpdateService umsEventUpdateService) {
        this.umsEventUpdateService = umsEventUpdateService;
    }

    @Bean
    public Consumer<UmsUserEvent> userMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            int userId = event.getUserId();
            Member member = event.getMember();

            switch (event.getEventType()) {

                case NEW_ACCOUNT:
                    umsEventUpdateService.addMember(member);
                    break;

                case UPDATE_ACCOUNT_INFO:
                    umsEventUpdateService.updateInfo(member);
                    break;

                case UPDATE_ACCOUNT_STATUS:
                    umsEventUpdateService.updateStatus(member);
                    break;

                case DELETE_ACCOUNT:
                    umsEventUpdateService.deleteMember(userId);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_STATUS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
