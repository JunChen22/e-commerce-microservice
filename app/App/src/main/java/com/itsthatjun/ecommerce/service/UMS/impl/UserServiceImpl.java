package com.itsthatjun.ecommerce.service.UMS.impl;

import com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberDTO;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.UMS.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final SecurityUtil securityUtil;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserServiceImpl(WebClient webClient, StreamBridge streamBridge, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.securityUtil = securityUtil;
    }

    @Override
    public Mono<MemberDetail> getInfo() {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId()) // Combine the JWT and memberId
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = UMS_SERVICE_URL + "/getInfo/";
                    LOG.debug("Will call the getInfo API on URL: {}", url);

                    return webClient.get()
                            .uri(url)
                            .header("Authorization", "Bearer " + jwt)
                            .header("X-MemberId", String.valueOf(memberId)) // TODO: might need to add this header to url
                            .retrieve()
                            .bodyToMono(MemberDetail.class)
                            .timeout(Duration.ofSeconds(5))
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling UMS service: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<MemberDetail> register(MemberDetail memberDetail) {
        return sendMessage("user-out-0", new UmsUserEvent(NEW_ACCOUNT, null, memberDetail, null))
                .then(Mono.just(memberDetail));
    }

    @Override
    public Mono<Void> updatePassword(String newPassword) {
        MemberDetail memberDetail = new MemberDetail();
        MemberDTO member = new MemberDTO();
        member.setPassword(newPassword);
        memberDetail.setMember(member);

        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("user-out-0", new UmsUserEvent(UPDATE_PASSWORD, memberId, memberDetail, jwt));
                }).then();
    }

    @Override
    public Mono<MemberDetail> updateInfo(MemberDetail memberDetail) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("user-out-0", new UmsUserEvent(UPDATE_ACCOUNT_INFO, memberId, memberDetail, jwt))
                            .then(Mono.just(memberDetail));
                });
    }

    @Override
    public Mono<AddressDTO> updateAddress(AddressDTO newAddress) {
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.setAddress(newAddress);

        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("user-out-0", new UmsUserEvent(UPDATE_ADDRESS, memberId, memberDetail, jwt))
                            .then(Mono.just(newAddress));
                });
    }

    @Override
    public Mono<Void> deleteAccount() {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("user-out-0", new UmsUserEvent(DELETE_ACCOUNT, memberId, null, jwt));
                }).then();
    }

    private Mono<Void> sendMessage(String bindingName, UmsUserEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
