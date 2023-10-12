package com.itsthatjun.ecommerce.service.UMS.impl;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.UMS.UserService;
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

import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums:8080/user";

    @Autowired
    public UserServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
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
    public Mono<Member> createMember(Member member, String operator) {
        return Mono.fromCallable(() -> {
            int memberId = member.getId();
            sendMessage("user-out-0", new UmsAdminUserEvent(NEW_ACCOUNT, memberId, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Member> updateMemberInfo(Member member, String operator) {
        return Mono.fromCallable(() -> {
            int memberId = member.getId();
            sendMessage("user-out-0", new UmsAdminUserEvent(UPDATE_ACCOUNT_INFO, memberId, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Member> updateMemberStatus(Member member, String operator) {
        return Mono.fromCallable(() -> {
            int memberId = member.getId();
            sendMessage("user-out-0", new UmsAdminUserEvent(UPDATE_ACCOUNT_STATUS, memberId, member, operator));
            return member;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> delete(int memberId, String operator) {
        return Mono.fromRunnable(() ->
                sendMessage("user-out-0", new UmsAdminUserEvent(DELETE_ACCOUNT, memberId, null, operator))
        ).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, UmsAdminUserEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
