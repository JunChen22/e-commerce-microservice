package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminBrandEvent;
import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminReviewEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.OmsUpdateIncomingEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.PmsReviewEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent;
import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.service.eventupdate.OmsEventUpdateService;
import com.itsthatjun.ecommerce.service.eventupdate.SmsEventUpdateService;
import com.itsthatjun.ecommerce.service.impl.BrandServiceImpl;
import com.itsthatjun.ecommerce.service.impl.ProductServiceImpl;
import com.itsthatjun.ecommerce.service.impl.ReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent.Type.UPDATE_SALE_PRICE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final BrandServiceImpl brandService;

    private final ProductServiceImpl productService;

    private final ReviewServiceImpl reviewService;

    private final OmsEventUpdateService omsEventUpdateService;

    private final SmsEventUpdateService smsEventUpdateService;

    @Autowired
    public MessageProcessorConfig(BrandServiceImpl brandService, ProductServiceImpl productService, ReviewServiceImpl reviewService,
                                  OmsEventUpdateService omsEventUpdateService, SmsEventUpdateService smsEventUpdateService) {
        this.brandService = brandService;
        this.productService = productService;
        this.reviewService = reviewService;
        this.omsEventUpdateService = omsEventUpdateService;
        this.smsEventUpdateService = smsEventUpdateService;
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
    public Consumer<PmsAdminBrandEvent> adminBrandMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            Brand brand = event.getBrand();
            switch (event.getEventType()) {

                case CREATE:
                    brandService.createBrand(brand);
                    break;

                case UPDATE:
                    brandService.updateBrand(brand);
                    break;

                case DELETE:
                    int brandId = event.getBrandId();
                    brandService.deleteBrand(brandId);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<PmsAdminProductEvent> adminProductMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            Product product = event.getProductDetail().getProduct();
            List<ProductSku> skuList = event.getProductDetail().getSkuVariants();
            switch (event.getEventType()) {

                case NEW_PRODUCT:
                    productService.createProduct(product, skuList);
                    break;

                case NEW_PRODUCT_SKU:
                    productService.addProductSku(product, skuList.get(0));
                    break;

                case UPDATE_PRODUCT_INFO:
                    productService.updateProductInfo(product);
                    break;

                case UPDATE_PRODUCT_STATUS:
                    productService.updateProductStatus(product);
                    break;

                case UPDATE_PRODUCT_SKU_STATUS:
                    productService.updateProductSkuStatus(skuList.get(0));
                    break;

                case UPDATE_STOCK:
                    int addedStock = event.getProductDetail().getStock();
                    productService.updateProductStock(skuList.get(0), addedStock);
                    break;

                case UPDATE_PRODUCT_PRICE:
                    productService.updateProductPrice(skuList);
                    break;

                case REMOVE_PRODUCT_SKU:
                    productService.removeProductSku(skuList.get(0));
                    break;

                case DELETE_PRODUCT:
                    productService.deleteProduct(product.getId());
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<PmsAdminReviewEvent> adminReviewMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            switch (event.getEventType()) {
                case CREATE:
                    reviewService.adminCreateReview(event.getReview().getReview(), event.getReview().getPicturesList());
                    break;

                case UPDATE:
                    reviewService.adminUpdateReview(event.getReview().getReview(), event.getReview().getPicturesList());
                    break;

                case DELETE:
                    int reviewId = event.getReviewId();
                    reviewService.adminDeleteReview(reviewId);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsUpdateIncomingEvent> omsProductMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Map<String, Integer> skuQuantityMap = event.getProductMap();
            switch (event.getEventType()) {

                case UPDATE_PURCHASE:
                    omsEventUpdateService.updatePurchase(skuQuantityMap);
                    break;

                case UPDATE_PURCHASE_PAYMENT:
                    omsEventUpdateService.updatePurchasePayment(skuQuantityMap);
                    break;

                case UPDATE_RETURN:
                    omsEventUpdateService.updateReturn(skuQuantityMap);
                    break;

                case UPDATE_FAIL_PAYMENT:
                    omsEventUpdateService.updateFailPayment(skuQuantityMap);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_PURCHASE, " +
                            "UPDATE_PURCHASE_PAYMENT, UPDATE_RETURN and UPDATE_FAIL_PAYMENT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<SmsUpdateIncomingEvent> smsProductMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            List<ProductSku> skuList = event.getSkuList();
            if (event.getEventType() == UPDATE_SALE_PRICE) {
                smsEventUpdateService.updateSale(skuList);
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_SALE_PRICE" +
                        " event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
