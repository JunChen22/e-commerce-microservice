package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent;
import com.itsthatjun.ecommerce.mbg.mapper.MemberLoginLogMapper;
import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberExample;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsLogUpdateEvent.Type.New_LOGIN;

@Service
public class MemberServiceImpl implements UserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final MemberLoginLogMapper loginLogMapper;

    private final JwtTokenUtil jwtTokenUtil;

    private final StreamBridge streamBridge;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, MemberLoginLogMapper loginLogMapper, JwtTokenUtil jwtTokenUtil, StreamBridge streamBridge) {
        this.memberMapper = memberMapper;
        this.loginLogMapper = loginLogMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.streamBridge = streamBridge;
    }

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = getMemberByUsername(username);
        if (member != null & member.getStatus() == 1) {
            return new CustomUserDetail(member);
        }
        //TODO: wasn't invoked when entering wrong username
        throw new UsernameNotFoundException("Username not found");
    }

    @Override
    public String login(String username, String password) {

        String token = "";
        try{
            CustomUserDetail userDetails = loadUserByUsername(username);
            // decode password to compare
            if (!passwordEncoder().matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("incorrect password");
            }

            // Authorities shouldn't be giving during validation
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, Collections.emptyList());   // provide empty list and will user userDetail's getAuthorities
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtTokenUtil.generateToken(userDetails);

            int userId = userDetails.getUserId();
            MemberLoginLog newLogin = new MemberLoginLog();
            newLogin.setId(userId);
            newLogin.setLoginTime(new Date());
            // newLogin.setIpAddress();  TODO: set IP address
            loginLogMapper.insert(newLogin);
            sendUmsLogUpdateMessage("authLog-out-0", new UmsLogUpdateEvent(New_LOGIN, userId, newLogin));
        } catch (AuthenticationException e) {
            // TODO: add a login error exception
            System.out.println("login error");
        }
        return token;
    }

    @Override
    public Member getMemberByUsername(String username) {
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(username).andStatusEqualTo(1);
        List<Member> memberList = memberMapper.selectByExample(example);

        if (memberList != null && !memberList.isEmpty()) {
            return memberList.get(0);
        }
        return null;
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void sendUmsLogUpdateMessage(String bindingName, UmsLogUpdateEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
