package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.admin.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsLogUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.outgoing.AddressDTO;
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

            int userId = event.getMember().getId();
            Member member = event.getMember();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case NEW_ACCOUNT:
                    memberService.createMember(member, operator).subscribe();
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateMemberInfo(member, operator).subscribe();
                    break;

                case UPDATE_ACCOUNT_STATUS:
                    memberService.updateMemberStatus(member, operator).subscribe();
                    break;

                case DELETE_ACCOUNT:
                    memberService.deleteMember(userId, operator).subscribe();
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
    public Consumer<UmsUserEvent> userMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            MemberDetail memberDetail = event.getMemberDetail();
            Integer userId = event.getUserId();
            switch (event.getEventType()) {
                case NEW_ACCOUNT:
                    memberService.register(memberDetail).subscribe();
                    break;

                case UPDATE_PASSWORD:

                    String newPassword = memberDetail.getPassword();
                    memberService.updatePassword(userId, newPassword).subscribe();
                    break;

                case UPDATE_ACCOUNT_INFO:
                    memberService.updateInfo(memberDetail, userId).subscribe();
                    break;

                case UPDATE_ADDRESS:
                    AddressDTO newAddress = memberDetail.getAddress();
                    memberService.updateAddress(event.getUserId(), newAddress).subscribe();
                    break;

                case DELETE_ACCOUNT:
                    memberService.deleteAccount(event.getUserId()).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_PASSWORD, UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_INFO, UPDATE_ADDRESS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
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
                memberService.createLoginLog(newLog).subscribe();
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected New_LOGIN and LOG_OFF event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        };
    }
}
