package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.UmsUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final MemberServiceImpl memberService;

    @Autowired
    public MessageProcessorConfig(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @Bean
    public Consumer<UmsUserEvent> articleMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            int userId = event.getUserId();
            Member member = event.getMember();

            switch (event.getEventType()) {

                case NEW_ACCOUNT:
                    memberService.addMember(member);
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateInfo(member);
                    break;

                case UPDATE_ACCOUNT_STATUS:
                    memberService.updateStatus(member);
                    break;

                case DELETE_ACCOUNT:
                    memberService.deleteMember(userId);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_STATUS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
