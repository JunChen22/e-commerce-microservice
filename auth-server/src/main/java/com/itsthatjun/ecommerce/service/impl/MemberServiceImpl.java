package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent;
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

import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent.Type.LOG_OFF;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent.Type.LOG_IN;

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
        // TODO: Add a check for the user's status (e.g. active, inactive, etc.)
        //      when no user is found, throw a UsernameNotFoundException
        return memberRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .map(CustomUserDetail::new);  // CustomUserDetail should implement UserDetails
    }

    @Override
    public Mono<Void> memberLoginLog(UUID memberId) {
        MemberActivityLog activityLog = new MemberActivityLog();
        activityLog.setMemberId(memberId);
        activityLog.setActivity(UserActivityType.LOGIN);
        activityLog.setIpAddress("127.0.0.1"); // TODO: set the actual IP address
        activityLog.setPlatformType(PlatformType.WEB);  // TODO: set the actual platform type

        return loginLogRepository.saveLog(activityLog) // Save the new login log
                .flatMap(savedLog -> 
                    // Send the log update message after saving the log
                    sendUmsLogUpdateMessage("authLog-out-0", new UmsLogUpdateEvent(LOG_IN, memberId, savedLog))
                ).then();
    }

    @Override
    public Mono<Void> memberLogoutLog(UUID memberId) {
        MemberActivityLog activityLog = new MemberActivityLog();
        activityLog.setMemberId(memberId);
        activityLog.setActivity(UserActivityType.LOGOFF);
        activityLog.setIpAddress("127.0.0.1"); // TODO: set the actual IP address
        activityLog.setPlatformType(PlatformType.WEB);  // TODO: set the actual platform type

        return loginLogRepository.saveLog(activityLog) // Save the new login log
                .flatMap(savedLog ->
                        // Send the log update message after saving the log
                        sendUmsLogUpdateMessage("authLog-out-0", new UmsLogUpdateEvent(LOG_OFF, memberId, savedLog))
                ).then();
    }

    private Mono<Void> sendUmsLogUpdateMessage(String bindingName, UmsLogUpdateEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            System.out.println("sending to binding: " + bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
