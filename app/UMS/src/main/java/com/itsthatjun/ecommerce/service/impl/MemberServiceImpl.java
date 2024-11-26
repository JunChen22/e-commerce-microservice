package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import com.itsthatjun.ecommerce.dto.model.MemberIconDTO;
import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import com.itsthatjun.ecommerce.enums.type.UpdateActionType;
import com.itsthatjun.ecommerce.exception.MemberNotFoundException;
import com.itsthatjun.ecommerce.model.entity.*;
import com.itsthatjun.ecommerce.repository.*;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsAuthUpdateEvent.Type.*;
import static com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent.Type.ONE_USER;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final VerificationServiceImpl verificationService;

    private final AddressRepository addressRepository;

    private final MemberChangeLogRepository changeLogRepository;

    private final MemberIconRepository iconRepository;

    private final MemberRepository memberRepository;

    private final StreamBridge streamBridge;

    private final DTOMapper dtoMapper;

    private final SecurityUtil securityUtil;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(VerificationServiceImpl verificationService, AddressRepository addressRepository, MemberChangeLogRepository changeLogRepository,
                             MemberIconRepository iconRepository, MemberRepository memberRepository, StreamBridge streamBridge,
                             DTOMapper dtoMapper, SecurityUtil securityUtil, PasswordEncoder passwordEncoder) {
        this.verificationService = verificationService;
        this.addressRepository = addressRepository;
        this.changeLogRepository = changeLogRepository;
        this.iconRepository = iconRepository;
        this.memberRepository = memberRepository;
        this.streamBridge = streamBridge;
        this.dtoMapper = dtoMapper;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Boolean> checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public Mono<MemberDetail> getInfo() {
        return securityUtil.getMemberId()
                .flatMap(memberId -> memberRepository.findById(memberId)
                        .switchIfEmpty(Mono.error(new MemberNotFoundException("Member not found: " + memberId)))
                        .flatMap(this::buildMemberDetail)
                );
    }

    private Mono<MemberDetail> buildMemberDetail(Member member) {
        // Wrap the MapStruct mapping in Mono.fromCallable to make it non-blocking
        Mono<MemberDTO> memberDTOMono = Mono.fromCallable(() -> dtoMapper.memberToMemberDTO(member));

        Mono<AddressDTO> addressMono = addressRepository.findByMemberId(member.getId())
                .flatMap(address -> Mono.just(dtoMapper.addressToAddressDTO(address)));

        Mono<MemberIconDTO> iconMono = iconRepository.findByMemberId(member.getId())
                .flatMap(icon -> Mono.just(dtoMapper.memberIconToMemberIconDTO(icon)));

        // Combine the results of the three asynchronous calls
        return Mono.zip(memberDTOMono, addressMono, iconMono)
                .map(tuple -> {
                    MemberDTO memberDTO = tuple.getT1();
                    AddressDTO addressDTO = tuple.getT2();
                    MemberIconDTO memberIconDTO = tuple.getT3();

                    // Return the MemberDetail with all the mapped and fetched data
                    return new MemberDetail(memberDTO, addressDTO, memberIconDTO);
                });
    }

    @Transactional
    @Override
    public Mono<MemberDetail> register(MemberDetail memberDetail) {
        Member newMember = dtoMapper.memberDTOToMember(memberDetail.getMember());
        Address address = dtoMapper.addressDTOToAddress(memberDetail.getAddress());
        MemberIcon icon = dtoMapper.memberIconDTOToMemberIcon(memberDetail.getIcon());

        newMember.setPassword(passwordEncoder.encode(newMember.getPassword()));
        newMember.setStatus(AccountStatus.ACTIVE);
        newMember.setLifecycleStatus(LifeCycleStatus.NORMAL);
        newMember.setVerifiedStatus(VerificationStatus.NOT_VERIFIED);

        return memberRepository.saveMember(newMember)
                .flatMap(member -> {
                    address.setMemberId(member.getId());
                    icon.setMemberId(member.getId());

                    return Mono.when(
                            addressRepository.save(address),
                            iconRepository.save(icon)
                    ).thenReturn(member); // Ensures chaining continues after saving both address and icon
                })
                .flatMap(savedMember -> {
                    return Mono.when(
                            createChangeLog(savedMember.getId(), UpdateActionType.CREATE,"create account", "user"),
                            verificationService.sendEmailVerification(savedMember.getId(), savedMember.getEmail()), // send out email to verify email
                            sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(NEW_ACCOUNT, savedMember.getId(), savedMember))
                    ).thenReturn(memberDetail); // Final return of the `MemberDetail` object
                })
                .doOnError(e -> LOG.error("Error registering new member: {}", e.getMessage()))
                .onErrorResume(e -> {
                    // Handle error (rollback, custom message, etc.)
                    return Mono.error(new RuntimeException("Failed to register member", e));
                });
    }

    @Override
    public Mono<MemberDTO> updatePassword(MemberDTO member, UUID memberId) {
        return memberRepository.findById(memberId)
                .flatMap(existingMember -> {
                    String currentPassword = existingMember.getPassword();
                    String newPassword = member.getPassword();

                    if (newPassword.equals(currentPassword)) {
                        return Mono.error(new IllegalArgumentException("New password cannot be the same as the current password"));
                    }

                    String newEncodedPassword = passwordEncoder.encode(newPassword);
                    existingMember.setPassword(newEncodedPassword);

                    // Create necessary events for sending notifications
                    UserInfo userInfo = new UserInfo(existingMember.getName(), existingMember.getEmail());
                    UmsEmailEvent emailEvent = new UmsEmailEvent(ONE_USER, userInfo, "Update password", "user");
                    UmsAuthUpdateEvent authEvent = new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, memberId, existingMember);

                    // Reactive flow: update password, create change log, send notifications
                    return memberRepository.updateMemberPassword(existingMember) // Update password reactively
                            .then(createChangeLog(memberId, UpdateActionType.UPDATE, "update account password", "user"))
                            .then(sendUserNotificationMessage("umsEmail-out-0", emailEvent))
                            .then(sendAuthUpdateMessage("authUpdate-out-0", authEvent))
                            .then(Mono.just(existingMember)); // Return the updated member as Mono
                })
                .map(dtoMapper::memberToMemberDTO) // Convert updated member to MemberDTO
                .doOnError(e -> LOG.error("Error updating password for member {}: {}", memberId, e.getMessage()))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update password", e))); // Handle errors
    }

    @Override
    public Mono<MemberDTO> updateInfo(MemberDTO member, UUID memberId) {
        Member updateMember = dtoMapper.memberDTOToMember(member);
        updateMember.setId(memberId);

        return memberRepository.updateMemberInfo(updateMember)
                .then(createChangeLog(member.getId(), UpdateActionType.UPDATE,"update account information", "user"))
                .then(sendAuthUpdateMessage("authUpdate-out-0", new UmsAuthUpdateEvent(UPDATE_ACCOUNT_INFO, memberId, updateMember)
                ))
                .thenReturn(member);
    }

    @Override
    public Mono<AddressDTO> updateAddress(AddressDTO newAddress, UUID memberId) {
        return memberRepository.findById(memberId)
                .flatMap(member -> addressRepository.findByMemberId(memberId)
                        .flatMap(address -> {
                            Address updatedAddress = dtoMapper.addressDTOToAddress(newAddress);
                            updatedAddress.setId(address.getId());
                            updatedAddress.setMemberId(memberId);
                            return addressRepository.save(updatedAddress);
                        })
                        .then(createChangeLog(memberId, UpdateActionType.UPDATE, "update account address", "user"))
                        .thenReturn(newAddress));
    }

    @Override
    public Mono<Void> deleteAccount(UUID memberId) {
        return memberRepository.findById(memberId)
                .flatMap(member -> {
                    UserInfo userInfo = new UserInfo(member.getName(), member.getEmail());
                    UmsEmailEvent emailEvent = new UmsEmailEvent(ONE_USER, userInfo, "delete user", "user");

                    UmsAuthUpdateEvent authEvent = new UmsAuthUpdateEvent(DELETE_ACCOUNT, memberId, member);

                    return memberRepository.deleteMember(memberId)
                            .then(createChangeLog(member.getId(), UpdateActionType.DELETE, "delete account", "user"))
                            .then(sendUserNotificationMessage("umsEmail-out-0", emailEvent))
                            .then(sendAuthUpdateMessage("authUpdate-out-0", authEvent));
                })
                .doOnError(error -> LOG.error("Failed to delete account for member {}: {}", memberId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new RuntimeException("Account deletion failed", error)));
    }

    private Mono<Void> createChangeLog(UUID memberId, UpdateActionType updateAction, String description, String operator) {
        MemberChangeLog changeLog = new MemberChangeLog();
        changeLog.setMemberId(memberId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setDescription(description);
        changeLog.setOperator(operator);
        return changeLogRepository.saveLog(changeLog).then();
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

    private Mono<Void> sendAuthUpdateMessage(String bindingName, UmsAuthUpdateEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
