package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.enums.type.PlatformType;
import com.itsthatjun.ecommerce.enums.type.UserActivityType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MemberService {

    /**
     * Sends a log of member activity to UMS's message queue
     *
     * @param memberId the ID of the user to log
     * @return a Mono indicating completion
     */
    Mono<Void> memberActivityLog(UUID memberId, UserActivityType activityType, PlatformType platformType, String ipAddress);
}
