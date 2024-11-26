package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.event.outgoing.UmsEmailEvent;
import com.itsthatjun.ecommerce.repository.MemberRepository;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.security.verification.VerificationTokenUtil;
import com.itsthatjun.ecommerce.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class VerificationServiceImpl implements VerificationService {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationServiceImpl.class);

    private final RedisServiceImpl redisService;

    private final MemberRepository memberRepository;

    private final VerificationTokenUtil tokenUtil;

    private final SecurityUtil securityUtil;

    private final StreamBridge streamBridge;

    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Autowired
    public VerificationServiceImpl(RedisServiceImpl redisService, MemberRepository memberRepository, VerificationTokenUtil tokenUtil,
                                   SecurityUtil securityUtil, StreamBridge streamBridge) {
        this.redisService = redisService;
        this.memberRepository = memberRepository;
        this.tokenUtil = tokenUtil;
        this.securityUtil = securityUtil;
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<String> sendMobileVerification(UUID memberId, String mobile) {
        // TODO: redis and notification

        long AUTH_CODE_DURATION = 600; // 600 seconds or 10 minutes
        //redisService.set(email, , AUTH_CODE_DURATION);

        // send out email
        // sendUserNotificationMessage("umsEmail-out-0", new UmsEmailEvent(ONE_USER, new UserInfo(email), "Your auth code is: " + authCode, "system"));

        return Mono.just("Auth code: test");
    }

    @Override
    public Mono<Boolean> verifyAuthCode(String authCode) {
        // TODO: redis
        securityUtil.getMemberId();

        // check in redis for auth code

        // change member verification status to verified

        // or else return error message

        return Mono.just(authCode.equals("Auth code: test"));
    }

    @Override
    public Mono<String> sendEmailVerification(UUID memberId, String email) {
        return Mono.just("test");
//        return Mono.fromCallable(() -> tokenUtil.generateToken(memberId))
//                .doOnNext(token -> {
//                    LOG.debug("Generated a verification token for member {}", memberId);
//                    redisService.set(token, memberId.toString(), tokenUtil.getExpiration());
//                });
    }

    @Override
    public Mono<Boolean> verifyVerificationToken(String token) {
        return Mono.just(token.equals("test"));
//        return Mono.fromCallable(() -> {
//            UUID memberId = UUID.fromString(redisService.get(token));
//            return tokenUtil.validateToken(token, memberId);
//        });

//        return verificationTokenUtil.validateToken(verificationToken)
//                .flatMap(isValid -> {
//                    if (!isValid) {
//                        // If token validation fails, return Mono.error directly
//                        return Mono.error(new RuntimeException("Invalid verification token"));
//                    }
//
//                    // Extract the email from the token
//                    return verificationTokenUtil.extractEmail(verificationToken)
//                            .flatMap(email ->
//                                    memberRepository.findByEmail(email)
//                                            .flatMap(member -> {
//                                                if (member == null) {
//                                                    // Handle case when member is not found
//                                                    return Mono.error(new EmailNotFoundException("Member not found for email: ", email));
//                                                }
//
//                                                // Update the member's verification status
//                                                member.setVerifiedStatus(VerificationStatus.VERIFIED);
//                                                return memberRepository.updateMemberVerification(member)
//                                                        .thenReturn(true); // Return true if update was successful
//                                            })
//                                            .doOnError(e -> LOG.error("Error updating verification status for member {}: {}", email, e.getMessage()))
//                            );
//                })
//                .doOnError(e -> LOG.error("Error validating verification token: {}", e.getMessage()))
//                .onErrorResume(e -> {
//                    // Handle error for token validation and email update failures
//                    return Mono.error(new RuntimeException("Failed to update verification status", e));
//                });
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
