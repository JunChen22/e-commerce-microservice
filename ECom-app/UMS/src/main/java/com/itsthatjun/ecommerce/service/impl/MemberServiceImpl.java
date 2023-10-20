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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Date;
import java.util.List;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent.Type.*;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final MemberIconMapper iconMapper; // TODO: add icon to registration/update

    private final MemberLoginLogMapper loginLogMapper;

    private final AddressMapper addressMapper;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, MemberIconMapper iconMapper, MemberLoginLogMapper loginLogMapper,
                             AddressMapper addressMapper, StreamBridge streamBridge,
                             @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.memberMapper = memberMapper;
        this.iconMapper = iconMapper;
        this.loginLogMapper = loginLogMapper;
        this.addressMapper = addressMapper;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
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
        return Mono.fromCallable(() -> {
            MemberDetail newMemberDetail = internalRegister(memberDetail);
            return newMemberDetail;
        }).subscribeOn(jdbcScheduler);
    }

    private MemberDetail internalRegister(MemberDetail memberDetail) {
        Member newMember = memberDetail.getMember();
        Address address = memberDetail.getAddress();

        String newUserName = newMember.getUsername();
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(newUserName);
        List<Member> existing = memberMapper.selectByExample(example);

        if (!existing.isEmpty()) {
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
        return memberDetail;
    }

    @Override
    public Mono<MemberDetail> getInfo(int userId) {
        return Mono.fromCallable(() -> {
            MemberDetail memberDetail = new MemberDetail();
            Member member = memberMapper.selectByPrimaryKey(userId);
            memberDetail.setMember(member);

            AddressExample addressExample = new AddressExample();
            addressExample.createCriteria().andMemberIdEqualTo(userId);
            List<Address> addressList = addressMapper.selectByExample(addressExample);

            if (!addressList.isEmpty()) memberDetail.setAddress(addressList.get(0));

            return memberDetail;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updatePassword(int userId, String newPassword) {
        return Mono.fromCallable(() -> {
            Member member = memberMapper.selectByPrimaryKey(userId);
            String currentPassword = member.getPassword();
            String newEncodedPassword = passwordEncoder().encode(newPassword);

            if (passwordEncoder().matches(newPassword, currentPassword)) return member; // TODO: send reminder, same password

            member.setPassword(newEncodedPassword);
            memberMapper.updateByPrimaryKey(member);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, member));
            return member;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateInfo(MemberDetail memberDetail) {
        return Mono.fromCallable(() -> {
            int userId = memberDetail.getMember().getId();
            Member member = memberMapper.selectByPrimaryKey(userId);

            Member updateMember = memberDetail.getMember();

            if (!member.getName().equals(updateMember.getName())) {
                sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updateMember));
            }

            memberMapper.updateByPrimaryKey(updateMember);
            return updateMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Address> updateAddress(int userId, Address newAddress) {
        return Mono.fromCallable(() -> {
            newAddress.setMemberId(userId);
            addressMapper.updateByPrimaryKey(newAddress);
            return newAddress;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteAccount(int userId) {
        return Mono.fromRunnable(() -> {
            Member member = memberMapper.selectByPrimaryKey(userId);
            member.setDeleteStatus(1);
            member.setStatus(0);

            memberMapper.updateByPrimaryKey(member);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Mono<Void> createLoginLog(MemberLoginLog loginLog) {
        return Mono.fromRunnable(
                () ->loginLogMapper.insert(loginLog)
        ).subscribeOn(jdbcScheduler).then();
    }

    // delete - status change, kept for archieve
    // send update to auth to remove it, status change to 0 too

    // status change, kept on database but need to check status for login

    // update info
    // send update to auth

    // ================= Admin actions ===================
    @Override
    public Mono<Member> createMember(Member newMember) {
        return Mono.fromCallable(() -> {
            String newUserName = newMember.getUsername();
            MemberExample example = new MemberExample();
            example.createCriteria().andUsernameEqualTo(newUserName);
            List<Member> existing = memberMapper.selectByExample(example);

            if (!existing.isEmpty()) {
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
            return newMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Member> getAllUser() {
        return Mono.fromCallable(() -> {
            List<Member> memberList = memberMapper.selectByExample(new MemberExample());
            return memberList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<MemberDetail> getMemberDetailByUserId(int userId) {
        return Mono.fromCallable(() -> {
            // TODO: might make a DAO for this
            MemberDetail memberDetail = new MemberDetail();
            Member member = memberMapper.selectByPrimaryKey(userId);
            memberDetail.setMember(member);

            AddressExample addressExample = new AddressExample();
            addressExample.createCriteria().andMemberIdEqualTo(userId);
            List<Address> addressList = addressMapper.selectByExample(addressExample);

            if (!addressList.isEmpty()) memberDetail.setAddress(addressList.get(0));
            return memberDetail;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<MemberLoginLog> getMemberLoginFrequency(int userId) {
        return Mono.fromCallable(() -> {
            MemberLoginLogExample loginLogExample = new MemberLoginLogExample();
            loginLogExample.createCriteria().andMemberIdEqualTo(userId);
            List<MemberLoginLog> loginLogList = loginLogMapper.selectByExample(loginLogExample);
            return loginLogList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateMemberInfo(Member updatedMember) {
        return Mono.fromCallable(() -> {
            int userId = updatedMember.getId();
            memberMapper.updateByPrimaryKey(updatedMember);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updatedMember));
            return updatedMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateMemberStatus(Member updatedMember) {
        return Mono.fromCallable(() -> {
            int userId = updatedMember.getId();
            memberMapper.updateByPrimaryKey(updatedMember);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_STATUS, userId, updatedMember));
            return updatedMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> deleteMember(int userId) {
        return Mono.fromCallable(() -> {
            Member member = memberMapper.selectByPrimaryKey(userId);
            member.setStatus(0);
            member.setDeleteStatus(1);
            memberMapper.updateByPrimaryKey(member);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
            return member;
        }).subscribeOn(jdbcScheduler);
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void sendAuthUpdateMessage(String bindingName, UmsAuthUpdateEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
