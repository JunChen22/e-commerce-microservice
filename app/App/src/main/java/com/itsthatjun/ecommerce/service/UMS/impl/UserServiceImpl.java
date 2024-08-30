package com.itsthatjun.ecommerce.service.UMS.impl;

import com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserServiceImpl(WebClient webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<MemberDetail> getInfo(int userId) {
        String url = UMS_SERVICE_URL + "/getInfo";
        LOG.debug("Will call the list API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(MemberDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<MemberDetail> register(MemberDetail memberDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("user-out-0", new UmsUserEvent(NEW_ACCOUNT, null, memberDetail));
            return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<String> updatePassword(String newPassword, int userId) {
        return Mono.fromCallable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setPassword(newPassword);
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_PASSWORD, userId, memberDetail));
            return newPassword;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<MemberDetail> updateInfo(MemberDetail memberDetail, int userId) {
        return Mono.fromCallable(() -> {
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ACCOUNT_INFO, userId, memberDetail));
            return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AddressDTO> updateAddress(AddressDTO newAddress, int userId) {
        return Mono.fromCallable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setAddress(newAddress);
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ADDRESS, userId, memberDetail));
            return newAddress;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteAccount(int userId) {
        return Mono.fromRunnable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            sendMessage("user-out-0", new UmsUserEvent(DELETE_ACCOUNT, userId, memberDetail));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, UmsUserEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
