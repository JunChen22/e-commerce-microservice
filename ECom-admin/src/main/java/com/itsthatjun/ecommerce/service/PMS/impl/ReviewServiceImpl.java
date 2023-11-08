package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
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

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminReviewEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms/review";

    @Autowired
    public ReviewServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<ProductReview> getProductReviews(int productId) {
        String url = PMS_SERVICE_URL + "/getAllProductReview/" + productId;

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<ProductReview> getProductReviewsByUser(int useId) {
        String url = PMS_SERVICE_URL + "/admin/getAllReviewByUser/" + useId;

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ProductReview> createProductReview(ProductReview review, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("review-out-0", new PmsAdminReviewEvent(CREATE, review, operator));
            return review;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductReview> updateProductReviews(ProductReview updatedReview, String operator) {
        return Mono.fromCallable(() -> {
            int reviewId = updatedReview.getReview().getId();
            sendMessage("review-out-0", new PmsAdminReviewEvent(UPDATE, updatedReview, operator));
            return updatedReview;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteProductReviews(int reviewId, String operator) {
        return Mono.fromRunnable(() -> {
            Review review = new Review();
            review.setId(reviewId);
            ProductReview productReview = new ProductReview();
            productReview.setReview(review);

            sendMessage("review-out-0", new PmsAdminReviewEvent(DELETE, productReview, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, PmsAdminReviewEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
