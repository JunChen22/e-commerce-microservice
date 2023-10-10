package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Review;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminReviewEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/review")
@Api(tags = "Product related", description = "CRUD a specific product reviews")
public class ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms:8080/review";

    @Autowired
    public ReviewController(WebClient.Builder webClient, StreamBridge streamBridge,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/getAllProductReview/{productId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        String url = PMS_SERVICE_URL + "/getAllProductReview/" + productId;

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/getAllReviewByUser/{useId}")
    @ApiOperation(value = "get all reviews made a user")
    public Flux<ProductReview> getProductReviewsByUser(@PathVariable int useId) {
        String url = PMS_SERVICE_URL + "/admin/getAllReviewByUser/" + useId;

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductReview.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @PostMapping("/create")
    @ApiOperation(value = "create review for a product")
    public void createProductReview(@RequestBody ProductReview review) {
        // TODO: review created time did not create automatically
        Mono.fromRunnable(() -> sendMessage("review-out-0", new PmsAdminReviewEvent(CREATE, review, null)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a review")
    public void updateProductReviews(@RequestBody ProductReview updatedReview) {
        int reviewId = updatedReview.getReview().getId();
        Mono.fromRunnable(() -> sendMessage("review-out-0", new PmsAdminReviewEvent(UPDATE, updatedReview, reviewId)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public void deleteProductReviews(@PathVariable int reviewId) {
        Mono.fromRunnable(() -> sendMessage("review-out-0", new PmsAdminReviewEvent(DELETE, null, reviewId)))
                .subscribeOn(publishEventScheduler).subscribe();
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
