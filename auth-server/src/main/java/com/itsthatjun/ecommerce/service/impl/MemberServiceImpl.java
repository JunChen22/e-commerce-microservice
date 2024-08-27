package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent;
import com.itsthatjun.ecommerce.mbg.mapper.MemberLoginLogMapper;
import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberExample;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
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
import java.util.List;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent.Type.New_LOGIN;

@Service
public class MemberServiceImpl implements ReactiveUserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final MemberLoginLogMapper loginLogMapper;

    private final StreamBridge streamBridge;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, MemberLoginLogMapper loginLogMapper, StreamBridge streamBridge) {
        this.memberMapper = memberMapper;
        this.loginLogMapper = loginLogMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException{
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(username).andStatusEqualTo(1);
        List<Member> memberList = memberMapper.selectByExample(example);

        if (memberList.isEmpty()) {
            return Mono.error(new UsernameNotFoundException("Username not found"));
        }

        Member member = memberList.get(0);

        return Mono.just(new CustomUserDetail(member));
    }

    @Override
    public void memberLoginLog(int userId) {
        MemberLoginLog newLogin = new MemberLoginLog();
        newLogin.setId(userId);
        newLogin.setLoginTime(new Date());
        // newLogin.setIpAddress();  TODO: set IP address
        loginLogMapper.insert(newLogin);
        sendUmsLogUpdateMessage("authLog-out-0", new UmsLogUpdateEvent(New_LOGIN, userId, newLogin));
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
