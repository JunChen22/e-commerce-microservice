package com.itsthatjun.ecommerce.service.UMS.impl;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.event.UmsAdminEmailEvent;
import com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.UMS.UserService;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminEmailEvent.Type.ALL_USER;
import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminEmailEvent.Type.ONE_USER;
import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AdminServiceImpl adminService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserServiceImpl(AdminServiceImpl adminService, WebClient.Builder webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.adminService = adminService;
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<MemberDetail> getUser(int userId) {
        String url = UMS_SERVICE_URL + "/admin/getMemberDetailByUserId/" + userId;

        return webClient.get().uri(url).retrieve().bodyToMono(MemberDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Flux<Member> getAllUser() {
        String url = UMS_SERVICE_URL + "/admin/getAllUser";

        return webClient.get().uri(url).retrieve().bodyToFlux(Member.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<MemberLoginLog> listAllLoginFrequency(int userId) {
        String url = UMS_SERVICE_URL + "/admin/getMemberLoginFrequency/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(MemberLoginLog.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Member> createMember(Member member) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("user-out-0", new UmsAdminUserEvent(NEW_ACCOUNT, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Member> updateMemberInfo(Member member) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("user-out-0", new UmsAdminUserEvent(UPDATE_ACCOUNT_INFO, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Member> updateMemberStatus(Member member) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("user-out-0", new UmsAdminUserEvent(UPDATE_ACCOUNT_STATUS, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> delete(int memberId) {
        return Mono.fromRunnable(() -> {
            String operator = adminService.getAdminName();
            Member member = new Member();
            member.setId(memberId);
            sendMessage("user-out-0", new UmsAdminUserEvent(DELETE_ACCOUNT, member, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, UmsAdminUserEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    public Mono<Void> sendUserEmail(int memberId, String message) {
        return Mono.fromRunnable(() -> {
            String operator = adminService.getAdminName();
            UmsAdminEmailEvent event =  new UmsAdminEmailEvent(ONE_USER, memberId, message, operator);
            sendEmailMessage("userEmail-out-0", event);
        }).subscribeOn(publishEventScheduler).then();
    }

    public Mono<Void> sendAllUserEmail(String message) {
        return Mono.fromRunnable(() -> {
            String operator = adminService.getAdminName();
            UmsAdminEmailEvent event =  new UmsAdminEmailEvent(ALL_USER, null, message, operator);
            sendEmailMessage("userEmail-out-0", event);
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendEmailMessage(String bindingName, UmsAdminEmailEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
