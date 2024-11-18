package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.PMS.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final SecurityUtil securityUtil;

    private final String PMS_SERVICE_URL = "http://pms/review";

    @Autowired
    public ReviewServiceImpl(WebClient webClient, StreamBridge streamBridge, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.securityUtil = securityUtil;
    }

    @Override
    public Mono<ProductReview> getDetailReview(int reviewId) {
        String url = PMS_SERVICE_URL + "/detail/" + reviewId;
        LOG.debug("Will call the getDetailReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Flux<ProductReview> getProductReviews(String skuCode) {
        String url = PMS_SERVICE_URL + "/getAllProductReview/" + skuCode;
        LOG.debug("Will call the getProductReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ProductReview> createProductReview(ProductReview newReview) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId()) // Combine the JWT and memberId
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("review-out-0", new PmsReviewEvent(CREATE_REVIEW, memberId, newReview))
                            .then(Mono.just(newReview));
                });
    }

    @Override
    public Mono<ProductReview> updateProductReviews(ProductReview newReview) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId()) // Combine the JWT and memberId
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("review-out-0", new PmsReviewEvent(UPDATE_REVIEW, memberId, newReview))
                            .then(Mono.just(newReview));
                });
    }

    @Override
    public Mono<Void> deleteProductReviews(String skuCode) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId()) // Combine the JWT and memberId
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("review-out-0", new PmsReviewEvent(DELETE_REVIEW, memberId, null))
                         .then();
                });
    }

    private Mono<Void> sendMessage(String bindingName, PmsReviewEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
