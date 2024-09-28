package com.itsthatjun.ecommerce.service.UMS.impl;

import com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.security.UserContext;
import com.itsthatjun.ecommerce.service.UMS.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final SecurityUtil securityUtil;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserServiceImpl(WebClient webClient, StreamBridge streamBridge, @Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
                           SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
        this.securityUtil = securityUtil;
    }

    @Override
    public int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserContext userContext = (UserContext) authentication.getPrincipal();
            return userContext.getUserId();
        }
        return -1; // TODO: throw exception
    }

    @Override
    public Mono<MemberDetail> getInfo() {
        return securityUtil.getJwtToken().zipWith(securityUtil.getUserId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    String url = UMS_SERVICE_URL + "/getInfo";
                    LOG.debug("Will call the list API on URL: {}", url);

                    return webClient.get()
                            .uri(url)
                            .header("Authorization", "Bearer " + jwt)
                            .retrieve()
                            .bodyToMono(MemberDetail.class)
                            .log(LOG.getName(), FINE)
                            .onErrorResume(error -> Mono.empty());
                });
    }

    @Override
    public Mono<MemberDetail> register(MemberDetail memberDetail) {
        return Mono.fromCallable(() -> {
            String jwt = "1";
            sendMessage("user-out-0", new UmsUserEvent(NEW_ACCOUNT, null, memberDetail, jwt));
            return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<String> updatePassword(String newPassword) {
        return Mono.fromCallable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setPassword(newPassword);
            int userId = 1;
            String jwt = "1";
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_PASSWORD, userId, memberDetail, jwt));
            return newPassword;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<MemberDetail> updateInfo(MemberDetail memberDetail) {
        return Mono.fromCallable(() -> {
            int userId = 1;
            String jwt = "1";
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ACCOUNT_INFO, userId, memberDetail, jwt));
            return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AddressDTO> updateAddress(AddressDTO newAddress) {
        return Mono.fromCallable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setAddress(newAddress);
            int userId = 1;
            String jwt = "1";
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ADDRESS, userId, memberDetail, jwt));
            return newAddress;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteAccount() {
        return Mono.fromRunnable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            int userId = 1;
            String jwt = "1";
            sendMessage("user-out-0", new UmsUserEvent(DELETE_ACCOUNT, userId, memberDetail, jwt));
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
