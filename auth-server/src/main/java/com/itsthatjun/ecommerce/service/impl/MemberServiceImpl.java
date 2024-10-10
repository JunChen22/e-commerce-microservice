package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent;
import com.itsthatjun.ecommerce.model.MemberLoginLog;
import com.itsthatjun.ecommerce.repository.MemberLoginLogRepository;
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

import java.util.Date;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent.Type.New_LOGIN;

@Service
public class MemberServiceImpl implements ReactiveUserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;

    private final MemberLoginLogRepository loginLogRepository;

    private final StreamBridge streamBridge;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, MemberLoginLogRepository loginLogRepository, StreamBridge streamBridge) {
        this.memberRepository = memberRepository;
        this.loginLogRepository = loginLogRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        // TODO: Add a check for the user's status (e.g. active, inactive, etc.)
        //      when no user is found, throw a UsernameNotFoundException
        return memberRepository.findByUsername(username)
                .map(CustomUserDetail::new);
    }

    @Override
    public Mono<Void> memberLoginLog(int memberId) {
        MemberLoginLog newLogin = new MemberLoginLog();
        newLogin.setMemberId(memberId);
        newLogin.setLoginTime(new Date());
        // TODO: set IP address (this should be done when you get the actual IP address)
        // newLogin.setIpAddress(ipAddress);

        return loginLogRepository.save(newLogin) // Save the new login log
                .flatMap(savedLog -> {
                    // Send the log update message after saving the log
                    sendUmsLogUpdateMessage("authLog-out-0", new UmsLogUpdateEvent(New_LOGIN, memberId, savedLog));
                    return Mono.empty();
                });
    }

    private void sendUmsLogUpdateMessage(String bindingName, UmsLogUpdateEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
