package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminBrandEvent;
import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.dto.event.admin.PmsAdminReviewEvent;
import com.itsthatjun.ecommerce.model.AdminProductDetail;
import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.model.entity.ProductSku;
import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.service.admin.AdminBrandServiceImpl;
import com.itsthatjun.ecommerce.service.admin.AdminProductServiceImpl;
import com.itsthatjun.ecommerce.service.admin.AdminReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AdminMessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMessageProcessorConfig.class);

    private final AdminBrandServiceImpl brandService;

    private final AdminProductServiceImpl productService;

    private final AdminReviewServiceImpl reviewService;

    @Autowired
    public AdminMessageProcessorConfig(AdminBrandServiceImpl brandService, AdminProductServiceImpl productService,
                                       AdminReviewServiceImpl reviewService) {
        this.brandService = brandService;
        this.productService = productService;
        this.reviewService = reviewService;
    }
//
//    @Bean
//    public Consumer<PmsAdminBrandEvent> adminBrandMessageProcessor() {
//        // lambda expression of override method accept
//        return event -> {
//            LOG.info("Process message created at {}...", event.getEventCreatedAt());
//            Brand brand = event.getBrand();
//            String operator = event.getOperator();
//
//            switch (event.getEventType()) {
//                case CREATE:
//                    brandService.adminCreateBrand(brand, operator).subscribe();
//                    break;
//
//                case UPDATE:
//                    brandService.adminUpdateBrand(brand, operator).subscribe();
//                    break;
//
//                case DELETE:
//                    int brandId = brand.getId();
//                    brandService.adminDeleteBrand(brandId, operator).subscribe();
//                    break;
//
//                default:
//                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
//                    LOG.warn(errorMessage);
//                    throw new RuntimeException(errorMessage);
//            }
//        };
//    }
//
//    @Bean
//    public Consumer<PmsAdminProductEvent> adminProductMessageProcessor() {
//        // lambda expression of override method accept
//        return event -> {
//            LOG.info("Process message created at {}...", event.getEventCreatedAt());
//            AdminProductDetail productDetail = event.getProductDetail();
//            Product product = productDetail.getProduct();
//            ProductSku sku = productDetail.getSkuVariants();
//            String operator = event.getOperator();
//
//            switch (event.getEventType()) {
//                case NEW_PRODUCT:
//                    productService.createProduct(productDetail, operator).subscribe();
//                    break;
//
//                case NEW_PRODUCT_SKU:
//                    productService.addProductSku(productDetail, operator).subscribe();
//                    break;
//
//                case UPDATE_PRODUCT_INFO:
//                    productService.updateProductInfo(product, operator).subscribe();
//                    break;
//
//                case UPDATE_PRODUCT_STATUS:
//                    productService.updateProductStatus(product, operator).subscribe();
//                    break;
//
//                case UPDATE_PRODUCT_SKU_STATUS:
//                    productService.updateProductSkuStatus(sku, operator).subscribe();
//                    break;
//
//                case UPDATE_STOCK:
//                    int addedStock = event.getProductDetail().getStock();
//                    productService.updateProductStock(sku, addedStock, operator).subscribe();
//                    break;
//
//                case UPDATE_PRODUCT_PRICE:
//                    productService.updateProductPrice(sku, operator).subscribe();
//                    break;
//
//                case REMOVE_PRODUCT_SKU:
//                    productService.removeProductSku(sku, operator).subscribe();
//                    break;
//
//                case DELETE_PRODUCT:
//                    productService.removeProduct(product.getId(), operator).subscribe();
//                    break;
//
//                default:
//                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
//                    LOG.warn(errorMessage);
//                    throw new RuntimeException(errorMessage);
//            }
//        };
//    }
//
//    @Bean
//    public Consumer<PmsAdminReviewEvent> adminReviewMessageProcessor() {
//        // lambda expression of override method accept
//        return event -> {
//            LOG.info("Process message created at {}...", event.getEventCreatedAt());
//            Review review = event.getReview().getReview();
//            String operator = event.getOperator();
//            switch (event.getEventType()) {
//                case CREATE:
//                    reviewService.adminCreateReview(review, event.getReview().getPicturesList(), operator).subscribe();
//                    break;
//
//                case UPDATE:
//                    reviewService.adminUpdateReview(review, event.getReview().getPicturesList(), operator).subscribe();
//                    break;
//
//                case DELETE:
//                    int reviewId = review.getId();
//                    reviewService.adminDeleteReview(reviewId, operator).subscribe();
//                    break;
//
//                default:
//                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", CREATE, UPDATE, and DELETE event";
//                    LOG.warn(errorMessage);
//                    throw new RuntimeException(errorMessage);
//            }
//        };
//    }
}
