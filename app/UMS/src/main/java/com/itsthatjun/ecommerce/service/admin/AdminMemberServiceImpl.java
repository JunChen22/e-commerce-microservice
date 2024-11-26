package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.admin.AdminMemberDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent;
import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import com.itsthatjun.ecommerce.enums.type.UpdateActionType;
import com.itsthatjun.ecommerce.exception.MemberNotFoundException;
import com.itsthatjun.ecommerce.model.entity.*;
import com.itsthatjun.ecommerce.repository.*;
import com.itsthatjun.ecommerce.service.AdminMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent.Type.*;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent.Type.DELETE_ACCOUNT;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent.Type.ALL_USER;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent.Type.ONE_USER;

@Service
public class AdminMemberServiceImpl implements AdminMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMemberServiceImpl.class);

    private final AddressRepository addressRepository;

    private final MemberChangeLogRepository changeLogRepository;

    private final MemberIconRepository iconRepository;

    private final MemberActivityLogRepository activityLogRepository;

    private final MemberRepository memberRepository;

    private final StreamBridge streamBridge;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminMemberServiceImpl(AddressRepository addressRepository, MemberChangeLogRepository changeLogRepository,
                                  MemberIconRepository iconRepository, MemberActivityLogRepository activityLogRepository,
                                  MemberRepository memberRepository, StreamBridge streamBridge, PasswordEncoder passwordEncoder) {
        this.addressRepository = addressRepository;
        this.changeLogRepository = changeLogRepository;
        this.iconRepository = iconRepository;
        this.activityLogRepository = activityLogRepository;
        this.memberRepository = memberRepository;
        this.streamBridge = streamBridge;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Flux<Member> listAllUser() {
        // TODO: might add a status filter to all and paginated list
        return memberRepository.findAll();
    }

    @Override
    public Flux<Member> listUser(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return memberRepository.findAllWithPage(offset, pageSize);
    }

    @Override
    public Mono<AdminMemberDetail> getMemberDetailByMemberId(UUID memberId) {
        return memberRepository.findById(memberId)
                .switchIfEmpty(Mono.error(new MemberNotFoundException("Member not found: " + memberId)))
                .flatMap(this::buildMemberDetail);
    }

    private Mono<AdminMemberDetail> buildMemberDetail(Member member) {
        Mono<Address> addressMono = addressRepository.findByMemberId(member.getId());
        Mono<MemberIcon> iconMono = iconRepository.findByMemberId(member.getId());
        Mono<List<MemberActivityLog>> loginLogListMono = activityLogRepository.findAllByMemberId(member.getId()).collectList();

        return Mono.zip(addressMono, iconMono, loginLogListMono)
                .map(tuple -> new AdminMemberDetail(member, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    public Flux<MemberActivityLog> getMemberActivityLog(UUID memberId) {
        return activityLogRepository.findAllByMemberId(memberId);
    }

    @Override
    public Mono<Member> createMember(Member newMember, String operator) {
        newMember.setPassword(passwordEncoder.encode(newMember.getPassword()));

        return memberRepository.saveMember(newMember)
                .flatMap(member ->
                        createUpdateLog(member.getId(), UpdateActionType.CREATE, "create member", operator)
                        .then(sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, member.getId(), newMember))
                        .thenReturn(member)));
    }

    public Mono<Member> updateMemberInfo(Member updatedMember, String operator) {
        return memberRepository.updateMemberInfo(updatedMember)
                .flatMap(member ->
                        createUpdateLog(member.getId(), UpdateActionType.UPDATE, "update member information", operator)
                        .then(sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, member.getId(), updatedMember))
                        .thenReturn(member)));
    }

    @Override
    public Mono<Member> updateMemberStatus(Member updatedMember, String operator) {
        return memberRepository.updateMemberStatus(updatedMember)
                .flatMap(member ->
                        createUpdateLog(member.getId(), UpdateActionType.UPDATE, "update member status", operator)
                        .then(sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_STATUS, member.getId(), updatedMember))
                        .thenReturn(member)));
    }

    @Override
    public Mono<Member> deleteMember(UUID memberId, String operator) {
        return memberRepository.findById(memberId)
                .switchIfEmpty(Mono.error(new MemberNotFoundException("Member not found: " + memberId)))
                .flatMap(member -> {
                    member.setStatus(AccountStatus.INACTIVE);
                    member.setLifecycleStatus(LifeCycleStatus.SOFT_DELETE);

                    return memberRepository.save(member)
                            .then(createUpdateLog(memberId, UpdateActionType.DELETE, "soft delete member",operator))
                            .then(sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(DELETE_ACCOUNT, memberId, member)))
                            .thenReturn(member);
                });
    }

    private Mono<Void> createUpdateLog(UUID memberId, UpdateActionType updateAction, String description, String operator) {
        MemberChangeLog changeLog = new MemberChangeLog();
        changeLog.setMemberId(memberId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setDescription(description);
        changeLog.setOperator(operator);
        return changeLogRepository.saveLog(changeLog).then();
    }

    private Mono<Void> sendAuthUpdateMessage(String bindingName, UmsAuthUpdateEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message<?> message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        }).subscribeOn(Schedulers.boundedElastic()) // Offload to a thread pool for potential blocking send() operation
        .then();
    }

    @Override
    public Mono<Void> sendUserNotification(UUID memberId, String message, String operator) {
        return memberRepository.findById(memberId)
                .switchIfEmpty(Mono.error(new MemberNotFoundException("Member not found: " + memberId)))
                .flatMap(member -> {
                    UserInfo userInfo = new UserInfo(member.getName(), member.getEmail());
                    UmsEmailEvent event = new UmsEmailEvent(ONE_USER, userInfo, message, operator);
                    sendUserNotificationMessage("umsEmail-out-0", event);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> sendAllUserNotification(String message, String operator) {
        return Mono.fromRunnable(() -> {
            UmsEmailEvent event = new UmsEmailEvent(ALL_USER, null, message, operator);
            sendUserNotificationMessage("umsEmail-out-0", event);
        }).then();
    }

    private Mono<Void> sendUserNotificationMessage(String bindingName, UmsEmailEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
