package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsActivityLogUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import com.itsthatjun.ecommerce.service.eventupdate.AuthEventUpdateService;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final MemberServiceImpl memberService;

    private final AuthEventUpdateService authEventUpdateService;

    @Autowired
    public MessageProcessorConfig(MemberServiceImpl memberService, AuthEventUpdateService authEventUpdateService) {
        this.memberService = memberService;
        this.authEventUpdateService = authEventUpdateService;
    }

    /**
     * Process user message
     */
    @Bean
    public Consumer<UmsUserEvent> userMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            MemberDetail memberDetail = event.getMemberDetail();
            UUID memberId = event.getMemberId();
            MemberDTO member = memberDetail.getMember();
            switch (event.getEventType()) {
                case NEW_ACCOUNT:
                    memberService.register(memberDetail).subscribe();
                    break;

                case UPDATE_PASSWORD:
                    memberService.updatePassword(member, memberId).subscribe();
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateInfo(member, memberId).subscribe();
                    break;

                case UPDATE_ADDRESS:
                    AddressDTO newAddress = memberDetail.getAddress();
                    memberService.updateAddress(newAddress, memberId).subscribe();
                    break;

                case DELETE_ACCOUNT:
                    memberService.deleteAccount(event.getMemberId()).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_PASSWORD, UPDATE_ACCOUNT_INFO, UPDATE_ADDRESS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    /**
     * Process activity log message
     */
    @Bean
    public Consumer<UmsActivityLogUpdateEvent> authMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            MemberActivityLog newLog = event.getActivityLog();

            switch (event.getEventType()) {
                case LOG_IN:
                    authEventUpdateService.createActivityLog(newLog).subscribe();
                    break;

                case LOG_OFF:
                    //authEventUpdateService.createActivityLog(newLog).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected New_LOGIN and LOG_OFF event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
