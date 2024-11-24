package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsActivityUpdateEvent;
import com.itsthatjun.ecommerce.enums.type.PlatformType;
import com.itsthatjun.ecommerce.enums.type.UserActivityType;
import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import com.itsthatjun.ecommerce.repository.MemberActivityLogRepository;
import com.itsthatjun.ecommerce.repository.MemberRepository;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class MemberServiceImpl implements ReactiveUserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;

    private final MemberActivityLogRepository loginLogRepository;

    private final StreamBridge streamBridge;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, MemberActivityLogRepository loginLogRepository, StreamBridge streamBridge) {
        this.memberRepository = memberRepository;
        this.loginLogRepository = loginLogRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .map(CustomUserDetail::new);
    }

    @Override
    public Mono<Void> memberActivityLog(UUID memberId, UserActivityType activityType, PlatformType platformType,String ipAddress) {
        MemberActivityLog activityLog = new MemberActivityLog();
        activityLog.setMemberId(memberId);
        activityLog.setActivity(activityType);
        activityLog.setIpAddress(ipAddress);
        activityLog.setPlatformType(platformType);

        UmsActivityUpdateEvent.Type eventType = mapToEventType(activityType);

        return loginLogRepository.saveLog(activityLog)
                .flatMap(savedLog ->
                    sendUmsLogUpdateMessage("authLog-out-0", new UmsActivityUpdateEvent(eventType, memberId, savedLog))
                ).then();
    }

    // TODO: upgrade to switch expression, case LOGIN -> UmsActivityUpdateEvent.Type.LOG_IN;,  when upgrade java 17 and/or spring boot 3
    public UmsActivityUpdateEvent.Type mapToEventType(UserActivityType activityType) {
        switch (activityType) {
            case LOGIN:
                return UmsActivityUpdateEvent.Type.LOG_IN;
            case LOGOFF:
                return UmsActivityUpdateEvent.Type.LOG_OFF;
            case FAILED_LOGIN:
                return UmsActivityUpdateEvent.Type.FAILED_LOGIN;
            case PASSWORD_CHANGE:
                return UmsActivityUpdateEvent.Type.PASSWORD_CHANGE;
            case SESSION_EXPIRED:
                return UmsActivityUpdateEvent.Type.SESSION_EXPIRED;
            case TWO_FA_SUCCESS:
                return UmsActivityUpdateEvent.Type.TWO_FA_SUCCESS;
            case TWO_FA_FAILED:
                return UmsActivityUpdateEvent.Type.TWO_FA_FAILED;
            case ACCOUNT_LOCKOUT:
                return UmsActivityUpdateEvent.Type.ACCOUNT_LOCKOUT;
            default:
                throw new IllegalArgumentException("Unknown activity type: " + activityType);
        }
    }

    private Mono<Void> sendUmsLogUpdateMessage(String bindingName, UmsActivityUpdateEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
