package com.itsthatjun.ecommerce.service.event;

import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.service.UmsService;
import com.itsthatjun.ecommerce.service.impl.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.util.logging.Level.FINE;

@Service
public class UmsServiceImpl implements UmsService {

    private static final Logger LOG = LoggerFactory.getLogger(UmsServiceImpl.class);

    private final String UMS_SERVICE_URL = "http://ums/user";

    private final WebClient webClient;

    private final EmailService emailService;

    @Autowired
    public UmsServiceImpl(WebClient webClient, EmailService emailService) {
        this.webClient = webClient;
        this.emailService = emailService;
    }

    public String sendUserMessage(UserInfo userInfo, String message) {
        emailService.sendEmail(userInfo.getEmail(), userInfo.getName(), message);
        return "message send";
    }

    public Mono<String> sendAllUserMessage(String message) {
        return retrieveAllUser()  // Stream user info reactively
                .delayElements(Duration.ofSeconds(2))  // Non-blocking delay of 2 seconds between emails
                .flatMap(userInfo -> emailService.sendEmail(userInfo.getEmail(), userInfo.getName(), message)
                        .then(Mono.just(userInfo)))  // Send email and return Mono
                .doOnError(e -> LOG.error("Error sending emails", e))  // Error handling
                .then(Mono.just("message sent"));  // Return success message when complete
    }

    public Flux<UserInfo> retrieveAllUser() {
        String url = UMS_SERVICE_URL + "/getAllUserInfo";

        return fetchUserInfo(url)
                .onErrorResume(e -> {
                    LOG.error("Failed to fetch user info from UMS", e);
                    return Flux.empty();  // Return empty Flux in case of error
                });
    }

    private Flux<UserInfo> fetchUserInfo(String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToFlux(UserInfo.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(e -> {
                    LOG.error("Failed to fetch user info from UMS", e);
                    return Flux.empty();
                });
    }
}
