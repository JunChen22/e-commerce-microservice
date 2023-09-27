package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent;
import com.itsthatjun.ecommerce.mbg.mapper.AddressMapper;
import com.itsthatjun.ecommerce.mbg.mapper.MemberIconMapper;
import com.itsthatjun.ecommerce.mbg.mapper.MemberLoginLogMapper;
import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent.Type.*;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final MemberIconMapper iconMapper;

    private final MemberLoginLogMapper loginLogMapper;

    private final AddressMapper addressMapper;

    private final StreamBridge streamBridge;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, MemberIconMapper iconMapper, MemberLoginLogMapper loginLogMapper,
                             AddressMapper addressMapper, StreamBridge streamBridge) {
        this.memberMapper = memberMapper;
        this.iconMapper = iconMapper;
        this.loginLogMapper = loginLogMapper;
        this.addressMapper = addressMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public String generateAuthCode(String telephone) {
        // TODO: redis
        return null;
    }

    @Override
    public String verifyAuthCode(String telephone, String authCode) {
        // TODO: redis
        return null;
    }

    @Override
    public Mono<MemberDetail> register(MemberDetail memberDetail) {

        Member newMember = memberDetail.getMember();
        Address address = memberDetail.getAddress();

        String newUserName = newMember.getUsername();
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(newUserName);
        List<Member> existing = memberMapper.selectByExample(example);

        if(!existing.isEmpty()){
            System.out.println("existing account");
            return null; // TODO: make exception for existing account
        }

        String passWord = newMember.getPassword();
        newMember.setPassword(passwordEncoder().encode(passWord));

        newMember.setCreatedAt(new Date());
        newMember.setStatus(1);

        memberMapper.insert(newMember);

        int newMemberId = newMember.getId();

        address.setMemberId(newMemberId);
        addressMapper.insert(address);

        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, newMemberId, newMember));

        return Mono.just(memberDetail);
    }

    @Override
    public Mono<MemberDetail> getInfo(int userId) {

        MemberDetail memberDetail = new MemberDetail();
        Member member = memberMapper.selectByPrimaryKey(userId);
        memberDetail.setMember(member);

        AddressExample addressExample = new AddressExample();
        addressExample.createCriteria().andMemberIdEqualTo(userId);
        List<Address> addressList = addressMapper.selectByExample(addressExample);

        if (!addressList.isEmpty()) memberDetail.setAddress(addressList.get(0));

        return Mono.just(memberDetail);
    }

    @Override
    public Mono<Member> updatePassword(int userId, String newPassword) {
        Member member = memberMapper.selectByPrimaryKey(userId);
        String currentPassword = member.getPassword();
        String newEncodedPassword = passwordEncoder().encode(newPassword);

        if (passwordEncoder().matches(newPassword, currentPassword)) {
            return Mono.just(member);
        }

        member.setPassword(newEncodedPassword);
        memberMapper.updateByPrimaryKey(member);

        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, member));
        return Mono.just(member);
    }

    @Override
    public Mono<Member> updateInfo(MemberDetail memberDetail) {
        int userId = memberDetail.getMember().getId();
        Member member = memberMapper.selectByPrimaryKey(userId);

        Member updateMember = memberDetail.getMember();

        if (member.getName() != updateMember.getName()) {
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updateMember));
        }

        memberMapper.updateByPrimaryKey(updateMember);
        return Mono.just(updateMember);
    }

    @Override
    public Mono<Address> updateAddress(int userId, Address newAddress) {
        newAddress.setMemberId(userId);
        addressMapper.updateByPrimaryKey(newAddress);
        return Mono.just(newAddress);
    }

    @Override
    public Mono<Void> deleteAccount(int userId) {

        Member member = memberMapper.selectByPrimaryKey(userId);
        member.setDeleteStatus(1);
        member.setStatus(0);

        memberMapper.updateByPrimaryKey(member);
        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
        return Mono.empty();
    }

    @Override
    public Mono<Void> createLoginLog(MemberLoginLog loginLog) {
        loginLogMapper.insert(loginLog);
        return Mono.empty();
    }


    // delete - status change, kept for archieve
    // send update to auth to remove it, status change to 0 too

    // status change, kept on database but need to check status for login

    // update info
    // send update to auth



    // ================= Admin actions ===================
    @Override
    public Mono<Member> createMember(Member newMember) {

        String newUserName = newMember.getUsername();
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(newUserName);
        List<Member> existing = memberMapper.selectByExample(example);

        if(!existing.isEmpty()){
            System.out.println("existing account");
            return null; // TODO: make exception for existing account
        }

        String passWord = newMember.getPassword();
        newMember.setPassword(passwordEncoder().encode(passWord));

        newMember.setCreatedAt(new Date());
        newMember.setStatus(1);

        memberMapper.insert(newMember);

        int userId = newMember.getId();

        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, userId, newMember));
        return Mono.just(newMember);
    }

    @Override
    public Flux<Member> getAllUser() {
        List<Member> memberList = memberMapper.selectByExample(new MemberExample());
        return Flux.fromIterable(memberList);
    }

    @Override
    public Mono<MemberDetail> getMemberDetailByUserId(int userId) {
        // TODO: might make a DAO for this

        MemberDetail memberDetail = new MemberDetail();
        Member member = memberMapper.selectByPrimaryKey(userId);
        memberDetail.setMember(member);

        AddressExample addressExample = new AddressExample();
        addressExample.createCriteria().andMemberIdEqualTo(userId);
        List<Address> addressList = addressMapper.selectByExample(addressExample);

        if (!addressList.isEmpty()) memberDetail.setAddress(addressList.get(0));

        return Mono.just(memberDetail);
    }

    @Override
    public Flux<MemberLoginLog> getMemberLoginFrequency(int userId) {

        MemberLoginLogExample loginLogExample = new MemberLoginLogExample();
        loginLogExample.createCriteria().andMemberIdEqualTo(userId);
        List<MemberLoginLog> loginLogList = loginLogMapper.selectByExample(loginLogExample);

        if (loginLogList.isEmpty()) return Flux.empty();

        return Flux.fromIterable(loginLogList);
    }

    @Override
    public Mono<Member> updateMemberInfo(Member updatedMember) {
        int userId = updatedMember.getId();
        memberMapper.updateByPrimaryKey(updatedMember);
        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updatedMember));
        return Mono.just(updatedMember);
    }

    @Override
    public Mono<Member> updateMemberStatus(Member updatedMember) {
        int userId = updatedMember.getId();
        memberMapper.updateByPrimaryKey(updatedMember);
        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_STATUS, userId, updatedMember));
        return Mono.just(updatedMember);
    }

    @Override
    public Mono<Member> deleteMember(int userId) {
        Member member = memberMapper.selectByPrimaryKey(userId);
        member.setStatus(0);
        member.setDeleteStatus(1);
        memberMapper.updateByPrimaryKey(member);
        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
        return Mono.just(member);
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void sendAuthUpdateMessage(String bindingName, UmsAuthUpdateEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
