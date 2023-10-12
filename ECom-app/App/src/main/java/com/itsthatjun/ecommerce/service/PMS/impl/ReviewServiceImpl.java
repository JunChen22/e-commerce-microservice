package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.service.PMS.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms/review";

    @Autowired
    public ReviewServiceImpl(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<ProductReview> getDetailReview(int reviewId) {
        String url = PMS_SERVICE_URL + "/detail/" + reviewId;
        LOG.debug("Will call the getDetailReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Flux<ProductReview> getProductReviews(int productId) {
        String url = PMS_SERVICE_URL + "/getAllProductReview/" + productId;
        LOG.debug("Will call the getDetailReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ProductReview> createProductReview(ProductReview newReview, int userId) {
        return Mono.fromCallable(() -> {
            sendMessage("review-out-0", new PmsReviewEvent(CREATE_REVIEW, userId, newReview.getReview(), newReview.getPicturesList()));
            return newReview;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductReview> updateProductReviews(ProductReview newReview, int userId) {
        return Mono.fromCallable(() -> {
            sendMessage("review-out-0", new PmsReviewEvent(UPDATE_REVIEW, userId, newReview.getReview(), newReview.getPicturesList()));
            return newReview;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteProductReviews(int reviewId, int userId) {
        Review review = new Review();
        review.setId(reviewId);
        return Mono.fromRunnable(() -> {
            sendMessage("review-out-0", new PmsReviewEvent(DELETE_REVIEW, userId, review, null));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, PmsReviewEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getUserId())
                .build();
        streamBridge.send(bindingName, message);
    }
}
