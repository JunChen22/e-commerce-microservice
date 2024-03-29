package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.AdminProductDetail;
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
import com.itsthatjun.ecommerce.service.admin.AdminProductServiceImpl;
import com.itsthatjun.ecommerce.service.eventupdate.OmsEventUpdateService;
import com.itsthatjun.ecommerce.service.eventupdate.SmsEventUpdateService;
import com.itsthatjun.ecommerce.service.impl.BrandServiceImpl;
import com.itsthatjun.ecommerce.service.impl.ReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent.Type.REMOVE_SALE;
import static com.itsthatjun.ecommerce.dto.event.incoming.SmsUpdateIncomingEvent.Type.UPDATE_SALE_PRICE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final BrandServiceImpl brandService;

    private final AdminProductServiceImpl productService;

    private final ReviewServiceImpl reviewService;

    private final OmsEventUpdateService omsEventUpdateService;

    private final SmsEventUpdateService smsEventUpdateService;

    @Autowired
    public MessageProcessorConfig(BrandServiceImpl brandService, AdminProductServiceImpl productService, ReviewServiceImpl reviewService,
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
            switch (event.getEventType()) {
                case CREATE_REVIEW:
                    reviewService.createReview(review, event.getPicturesList(), userId).subscribe();
                    break;

                case UPDATE_REVIEW:
                    reviewService.updateReview(review, event.getPicturesList(), userId).subscribe();
                    break;

                case DELETE_REVIEW:
                    int reviewId = review.getId();
                    reviewService.deleteReview(reviewId, userId).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_REVIEW, UPDATE_REVIEW, DELETE_REVIEW event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<PmsAdminBrandEvent> adminBrandMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            Brand brand = event.getBrand();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case CREATE:
                    brandService.adminCreateBrand(brand, operator).subscribe();
                    break;

                case UPDATE:
                    brandService.adminUpdateBrand(brand, operator).subscribe();
                    break;

                case DELETE:
                    int brandId = brand.getId();
                    brandService.adminDeleteBrand(brandId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<PmsAdminProductEvent> adminProductMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            AdminProductDetail productDetail = event.getProductDetail();
            Product product = productDetail.getProduct();
            ProductSku sku = productDetail.getSkuVariants();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case NEW_PRODUCT:
                    productService.createProduct(productDetail, operator).subscribe();
                    break;

                case NEW_PRODUCT_SKU:
                    productService.addProductSku(productDetail, operator).subscribe();
                    break;

                case UPDATE_PRODUCT_INFO:
                    productService.updateProductInfo(product, operator).subscribe();
                    break;

                case UPDATE_PRODUCT_STATUS:
                    productService.updateProductStatus(product, operator).subscribe();
                    break;

                case UPDATE_PRODUCT_SKU_STATUS:
                    productService.updateProductSkuStatus(sku, operator).subscribe();
                    break;

                case UPDATE_STOCK:
                    int addedStock = event.getProductDetail().getStock();
                    productService.updateProductStock(sku, addedStock, operator).subscribe();
                    break;

                case UPDATE_PRODUCT_PRICE:
                    productService.updateProductPrice(sku, operator).subscribe();
                    break;

                case REMOVE_PRODUCT_SKU:
                    productService.removeProductSku(sku, operator).subscribe();
                    break;

                case DELETE_PRODUCT:
                    productService.removeProduct(product.getId(), operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<PmsAdminReviewEvent> adminReviewMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            /* TODO:
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            Review review = event.getReview().getReview();
            String operator = event.getOperator();
            switch (event.getEventType()) {
                case CREATE:
                    reviewService.adminCreateReview(review, event.getReview().getPicturesList(), operator).subscribe();
                    break;

                case UPDATE:
                    reviewService.adminUpdateReview(review, event.getReview().getPicturesList(), operator).subscribe();
                    break;

                case DELETE:
                    int reviewId = review.getId();
                    reviewService.adminDeleteReview(reviewId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
             */
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
                    omsEventUpdateService.updatePurchase(skuQuantityMap).subscribe();
                    break;

                case UPDATE_PURCHASE_PAYMENT:
                    omsEventUpdateService.updatePurchasePayment(skuQuantityMap).subscribe();
                    break;

                case UPDATE_RETURN:
                    omsEventUpdateService.updateReturn(skuQuantityMap).subscribe();
                    break;

                case UPDATE_FAIL_PAYMENT:
                    omsEventUpdateService.updateFailPayment(skuQuantityMap).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_PURCHASE, " +
                            "UPDATE_PURCHASE_PAYMENT, UPDATE_RETURN and UPDATE_FAIL_PAYMENT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
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
                smsEventUpdateService.updateSale(skuList).subscribe();
            } else if (event.getEventType() == REMOVE_SALE) {
                smsEventUpdateService.removeSale(skuList).subscribe();
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_SALE_PRICE and REMOVE_SALE event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        };
    }
}
