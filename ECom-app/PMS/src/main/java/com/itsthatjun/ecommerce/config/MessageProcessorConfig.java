package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.PmsProductEvent;
import com.itsthatjun.ecommerce.dto.event.PmsReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.service.impl.ProductServiceImpl;
import com.itsthatjun.ecommerce.service.impl.ReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ProductServiceImpl productService;

    private final ReviewServiceImpl reviewService;

    @Autowired
    public MessageProcessorConfig(ProductServiceImpl productService, ReviewServiceImpl reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @Bean
    public Consumer<PmsReviewEvent> reviewMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            Review review = event.getReview();
            int userId = event.getUserId();
            switch (event.getEventType()){
                case CREATE_REVIEW:
                    reviewService.createReview(review, event.getPicturesList(), userId);
                    break;

                case UPDATE_REVIEW:
                    reviewService.updateReview(review, event.getPicturesList(), userId);
                    break;

                case DELETE_REVIEW:
                    int reviewId = review.getId();
                    reviewService.deleteReview(reviewId, userId);
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_REVIEW, UPDATE_REVIEW, DELETE_REVIEW event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<PmsProductEvent> productMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Map<String, Integer> skuQuantityMap = event.getProductMap();
            switch (event.getEventType()) {

                case UPDATE_PURCHASE:
                    productService.updatePurchase(skuQuantityMap);
                    break;

                case UPDATE_PURCHASE_PAYMENT:
                    productService.updatePurchasePayment(skuQuantityMap);
                    break;

                case UPDATE_RETURN:
                    productService.updateReturn(skuQuantityMap);
                    break;

                case UPDATE_FAIL_PAYMENT:
                    productService.updateFailPayment(skuQuantityMap);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_PURCHASE, " +
                            "UPDATE_PURCHASE_PAYMENT, UPDATE_RETURN and UPDATE_FAIL_PAYMENT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
