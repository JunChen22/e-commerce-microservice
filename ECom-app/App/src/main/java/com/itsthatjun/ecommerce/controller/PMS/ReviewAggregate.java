package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "Review controller", description = "Review Controller")
@RequestMapping("/review")
public class ReviewAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms/review";

    @Autowired
    public ReviewAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/detail/{reviewId}")
    @ApiOperation(value = "get detail of a review")
    public Mono<ProductReview> getDetailReview(@PathVariable int reviewId) {
        String url = PMS_SERVICE_URL + "/detail/" + reviewId;
        LOG.debug("Will call the getDetailReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @GetMapping("/getAllProductReview/{productId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        String url = PMS_SERVICE_URL + "/getAllProductReview/" + productId;
        LOG.debug("Will call the getDetailReview API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @PostMapping("/create")
    @ApiOperation(value = "create review for a product")
    public Mono<ProductReview> createProductReview(@RequestBody ProductReview newReview) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return Mono.fromCallable(() -> {
            sendMessage("review-out-0", new PmsReviewEvent(CREATE_REVIEW, userId, newReview.getReview(), newReview.getPicturesList()));
            return newReview;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a review")
    public Mono<ProductReview> updateProductReviews(@RequestBody ProductReview newReview) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();
        return Mono.fromCallable(() -> {
            sendMessage("review-out-0", new PmsReviewEvent(UPDATE_REVIEW, userId, newReview.getReview(), newReview.getPicturesList()));
            return newReview;
        }).subscribeOn(publishEventScheduler);
    }

    @DeleteMapping("/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(@PathVariable int reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

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
