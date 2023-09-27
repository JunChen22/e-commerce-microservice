package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.admin.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsLogUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.incoming.UmsLogUpdateEvent.Type.New_LOGIN;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final MemberServiceImpl memberService;

    @Autowired
    public MessageProcessorConfig(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @Bean
    public Consumer<UmsAdminUserEvent> adminUserMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            int userId = event.getUserId();
            Member member = event.getMember();

            switch (event.getEventType()) {

                case NEW_ACCOUNT:
                    memberService.createMember(member);
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateMemberInfo(member);
                    break;

                case UPDATE_ACCOUNT_STATUS:
                    memberService.updateMemberStatus(member);
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

    @Bean
    public Consumer<UmsUserEvent> userMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            MemberDetail memberDetail = event.getMemberDetail();

            switch (event.getEventType()) {
                case NEW_ACCOUNT:
                    memberService.register(memberDetail);
                    break;

                case UPDATE_PASSWORD:
                    int userId = memberDetail.getMember().getId();
                    String newPassword = memberDetail.getMember().getPassword();
                    memberService.updatePassword(userId, newPassword);
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateInfo(memberDetail);
                    break;

                case UPDATE_ADDRESS:
                    Address newAddress = memberDetail.getAddress();
                    memberService.updateAddress(memberDetail.getMember().getId(), newAddress);
                    break;

                case DELETE_ACCOUNT:
                    memberService.deleteAccount(memberDetail.getMember().getId());
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_PASSWORD, UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_INFO, UPDATE_ADDRESS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<UmsLogUpdateEvent> authMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            MemberLoginLog newLog = event.getLoginLog();
            if (event.getEventType() == New_LOGIN) {
                memberService.createLoginLog(newLog);
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected New_LOGIN and LOG_OFF event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
