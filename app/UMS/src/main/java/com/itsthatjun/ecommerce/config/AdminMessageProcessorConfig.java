package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.admin.UmsAdminEmailEvent;
import com.itsthatjun.ecommerce.dto.event.admin.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.model.entity.Member;
import com.itsthatjun.ecommerce.service.admin.AdminMemberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Consumer;

@Configuration
public class AdminMessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMessageProcessorConfig.class);

    private final AdminMemberServiceImpl adminMemberService;

    @Autowired
    public AdminMessageProcessorConfig(AdminMemberServiceImpl adminMemberService) {
        this.adminMemberService = adminMemberService;
    }

    /**
     * Process admin user message
     */
    @Bean
    public Consumer<UmsAdminUserEvent> adminUserMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            UUID memberId = event.getMember().getId();
            Member member = event.getMember();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case NEW_ACCOUNT:
                    adminMemberService.createMember(member, operator).subscribe();
                    break;

                case UPDATE_ACCOUNT_INFO:
                    adminMemberService.updateMemberInfo(member, operator).subscribe();
                    break;

                case UPDATE_ACCOUNT_STATUS:
                    adminMemberService.updateMemberStatus(member, operator).subscribe();
                    break;

                case DELETE_ACCOUNT:
                    adminMemberService.deleteMember(memberId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_ACCOUNT, " +
                            "UPDATE_ACCOUNT_INFO, UPDATE_ACCOUNT_STATUS and DELETE_ACCOUNT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    /**
     * Process admin email message
     */
    @Bean
    public Consumer<UmsAdminEmailEvent> adminEmailProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            String message = event.getMessage();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case ONE_USER:
                    UUID memberId = event.getMemberId();
                    adminMemberService.sendUserNotification(memberId, message, operator).subscribe();
                    break;

                case ALL_USER:
                    adminMemberService.sendAllUserNotification(message, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected" + "ONE_USER and ALL_USER event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
