package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.MemberDao;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.admin.AdminMemberDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.security.SecurityUtil;
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
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent.Type.ALL_USER;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent.Type.ONE_USER;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final MemberIconMapper iconMapper; // TODO: add icon to registration/update

    private final MemberLoginLogMapper loginLogMapper;

    private final AddressMapper addressMapper;

    private final MemberChangeLogMapper logMapper;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final MemberDao memberDao;

    private final DTOMapper dtoMapper;

    private final SecurityUtil securityUtil;

    private final RedisServiceImpl redisService;

    //@Value("${redis.key.orderId}")
    //private String REDIS_KEY_ORDER_ID;

    //@Value("${redis.database}")
    //private String REDIS_DATABASE;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, MemberIconMapper iconMapper, MemberLoginLogMapper loginLogMapper,
                             MemberChangeLogMapper logMapper, AddressMapper addressMapper, StreamBridge streamBridge,
                             @Qualifier("jdbcScheduler") Scheduler jdbcScheduler, MemberDao memberDao, DTOMapper dtoMapper,
                             RedisServiceImpl redisService, SecurityUtil securityUtil) {
        this.memberMapper = memberMapper;
        this.iconMapper = iconMapper;
        this.loginLogMapper = loginLogMapper;
        this.addressMapper = addressMapper;
        this.logMapper = logMapper;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
        this.memberDao = memberDao;
        this.dtoMapper = dtoMapper;
        this.redisService = redisService;
        this.securityUtil = securityUtil;
    }

    @Override
    public String generateAuthCode(String email) {
        // TODO: redis and notification

        long AUTH_CODE_DURATION = 600; // 600 seconds or 10 minutes
        //redisService.set(email, , AUTH_CODE_DURATION);

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
        Member newMember = dtoMapper.memberDetailToMember(memberDetail);
        Address address = dtoMapper.addressDTOToAddress(memberDetail.getAddress(), null);

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
        newMember.setStatus(1);
        memberMapper.insert(newMember);

        int newMemberId = newMember.getId();

        address.setMemberId(newMemberId);
        addressMapper.insert(address);

        createUpdateLog(newMemberId, "create account", "user");
        sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, newMemberId, newMember));

        return memberDetail;
    }

    @Override
    public Mono<MemberDetail> getInfo() {
        return securityUtil.getUserId()
                .flatMap(userId ->
                        Mono.fromCallable(() -> {
                            MemberDetail memberDetail = memberDao.getUserDetail(userId);
                            return memberDetail;
                        }).subscribeOn(jdbcScheduler)
                );
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

            createUpdateLog(member.getId(), "update account password", "user");
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, member));
            return member;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateInfo(MemberDetail memberDetail, int userId) {
        return Mono.fromCallable(() -> {
            Member member = memberMapper.selectByPrimaryKey(userId);
            Member updateMember = dtoMapper.memberDetailToMember(memberDetail);
            updateMember.setId(userId);

            if (!member.getName().equals(updateMember.getName())) {
                sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updateMember));
            }

            memberMapper.updateByPrimaryKeySelective(updateMember);
            createUpdateLog(member.getId(), "update account information", "user");

            return updateMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Address> updateAddress(int userId, AddressDTO addressDTO) {
        return Mono.fromCallable(() -> {
            AddressExample addressExample = new AddressExample();
            addressExample.createCriteria().andMemberIdEqualTo(userId);
            Address address = addressMapper.selectByExample(addressExample).get(0);

            Address newAddress = dtoMapper.addressDTOToAddress(addressDTO, address.getId());
            addressMapper.updateByPrimaryKeySelective(newAddress);

            createUpdateLog(userId, "update account address", "user");
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
            createUpdateLog(member.getId(), "delete account", "user");
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Mono<Void> createLoginLog(MemberLoginLog loginLog) {
        return Mono.fromRunnable(
                () -> loginLogMapper.insert(loginLog)
        ).subscribeOn(jdbcScheduler).then();
    }

    // delete - status change, kept for archive
    // send update to auth to remove it, status change to 0 too

    // status change, kept on database but need to check status for login

    // update info
    // send update to auth

    // ================= Admin actions ===================
    @Override
    public Flux<Member> getAllUser() {
        return Mono.fromCallable(() -> {
            List<Member> memberList = memberMapper.selectByExample(new MemberExample());
            return memberList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<AdminMemberDetail> getMemberDetailByUserId(int userId) {
        return Mono.fromCallable(() -> {
            AdminMemberDetail memberDetail = new AdminMemberDetail();
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
    public Mono<Member> createMember(Member newMember, String operator) {
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
            newMember.setStatus(1);

            memberMapper.insert(newMember);
            int userId = newMember.getId();

            createUpdateLog(userId, "create member", operator);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, userId, newMember));
            return newMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateMemberInfo(Member updatedMember, String operator) {
        return Mono.fromCallable(() -> {
            int userId = updatedMember.getId();
            memberMapper.updateByPrimaryKeySelective(updatedMember);
            createUpdateLog(userId, "update member information", operator);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, userId, updatedMember));
            return updatedMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> updateMemberStatus(Member updatedMember, String operator) {
        return Mono.fromCallable(() -> {
            int userId = updatedMember.getId();
            memberMapper.updateByPrimaryKeySelective(updatedMember);
            createUpdateLog(userId, "update member status", operator);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_STATUS, userId, updatedMember));
            return updatedMember;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Member> deleteMember(int userId, String operator) {
        return Mono.fromCallable(() -> {
            Member member = memberMapper.selectByPrimaryKey(userId);
            member.setStatus(0);
            member.setDeleteStatus(1);
            memberMapper.updateByPrimaryKeySelective(member);

            createUpdateLog(userId, "delete member", operator);
            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, userId, member));
            return member;
        }).subscribeOn(jdbcScheduler);
    }

    private void createUpdateLog(int userId, String updateAction, String operator) {
        MemberChangeLog changeLog = new MemberChangeLog();
        changeLog.setMemberId(userId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setOperator(operator);
        logMapper.insert(changeLog);
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

    public List<UserInfo> getAllUserInfo() {
        List<UserInfo> userInfoList = memberDao.getAllUserInfo();
        return userInfoList;
    }

    @Override
    public Mono<Void> sendUserNotification(int userId, String message, String operator) {
        return Mono.fromRunnable(() -> {
            UserInfo userInfo = memberDao.getUserInfo(userId);
            UmsEmailEvent event = new UmsEmailEvent(ONE_USER, userInfo, message, operator);
            sendUserNotificationMessage("umsEmail-out-0", event);
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Mono<Void> sendAllUserNotification(String message, String operator) {
        return Mono.fromRunnable(() -> {
            UmsEmailEvent event = new UmsEmailEvent(ALL_USER, null, message, operator);
            sendUserNotificationMessage("umsEmail-out-0", event);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void sendUserNotificationMessage(String bindingName, UmsEmailEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
