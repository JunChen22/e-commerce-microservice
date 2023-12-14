package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.event.admin.SmsAdminCouponEvent;
import com.itsthatjun.ecommerce.dto.event.admin.SmsAdminSaleEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.OmsCouponUpdateIncomingEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.OmsStockUpdateIncomingEvent;
import com.itsthatjun.ecommerce.dto.event.incoming.PmsProductUpdateIncomingEvent;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.service.eventupdate.OmsEventUpdateService;
import com.itsthatjun.ecommerce.service.eventupdate.PmsEventUpdateService;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import com.itsthatjun.ecommerce.service.impl.SalesServiceimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.incoming.OmsCouponUpdateIncomingEvent.Type.UPDATE_COUPON_USAGE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CouponServiceImpl couponService;

    private final SalesServiceimpl salesService;

    private final OmsEventUpdateService omsEventUpdateService;

    private final PmsEventUpdateService pmsEventUpdateService;

    @Autowired
    public MessageProcessorConfig(CouponServiceImpl couponService, SalesServiceimpl salesService, OmsEventUpdateService omsEventUpdateService,
                                  PmsEventUpdateService pmsEventUpdateService) {
        this.couponService = couponService;
        this.salesService = salesService;
        this.omsEventUpdateService = omsEventUpdateService;
        this.pmsEventUpdateService = pmsEventUpdateService;
    }

    @Bean
    public Consumer<SmsAdminCouponEvent> adminCouponMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Coupon coupon = event.getCouponSale().getCoupon();
            Map<String, Integer> skuQuantity = event.getCouponSale().getSkuQuantity();
            String operator = event.getOperator();
            switch (event.getEventType()) {
                case CREATE_COUPON:
                    couponService.createCoupon(coupon, skuQuantity, operator).subscribe();
                    break;

                case UPDATE_COUPON:
                    couponService.updateCoupon(coupon, skuQuantity, operator).subscribe();
                    break;

                case DELETE_COUPON:
                    int couponId = event.getCouponSale().getCouponId();
                    couponService.deleteCoupon(couponId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_COUPON, " +
                            "UPDATE_COUPON, and DELETE_COUPON event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<SmsAdminSaleEvent> adminSalesStockMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            OnSaleRequest saleRequest = event.getSaleRequest();
            String operator = event.getSaleRequest().getOperator();
            switch (event.getEventType()) {
                case CREATE_SALE_LIST:
                    salesService.createListSale(saleRequest, operator).subscribe();
                    break;

                case CREATE_SALE_BRAND:
                    salesService.createBrandSale(saleRequest, operator).subscribe();
                    break;

                case CREATE_SALE_CATEGORY:
                    salesService.createCategorySale(saleRequest, operator).subscribe();
                    break;

                case UPDATE_SALE_INFO:
                    salesService.updateSaleInfo(saleRequest, operator).subscribe();
                    break;

                case UPDATE_SALE_PRICE:
                    salesService.updateSalePrice(saleRequest, operator).subscribe();
                    break;

                case UPDATE_SALE_STATUS:
                    salesService.updateSaleStatus(saleRequest, operator).subscribe();
                    break;

                case DELETE_SALE:
                    int promotionId = saleRequest.getPromotionSale().getId();
                    salesService.delete(promotionId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_SALE, " +
                            "UPDATE_SALE_INFO, UPDATE_SALE_PRICE, UPDATE_SALE_STATUS and DELETE_SALE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<OmsCouponUpdateIncomingEvent> couponMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            if (event.getEventType() == UPDATE_COUPON_USAGE) {
                couponService.updateUsedCoupon(event.getCoupon(), event.getOrderId(), event.getMemberId()).subscribe();
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_COUPON_USAGE event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        };
    }

    @Bean
    public Consumer<OmsStockUpdateIncomingEvent> salesStockMessageProcessor() {
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
    public Consumer<PmsProductUpdateIncomingEvent> productStockMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Product newProduct = event.getProduct();
            ProductSku productSku = event.getProductSku();

            switch (event.getEventType()) {
                case NEW_PRODUCT:
                    pmsEventUpdateService.addProduct(newProduct, productSku).subscribe();
                    break;

                case NEW_PRODUCT_SKU:
                    pmsEventUpdateService.addProductSku(newProduct, productSku).subscribe();
                    break;

                case UPDATE_PRODUCT:
                    pmsEventUpdateService.updateProduct(newProduct, productSku).subscribe();
                    break;

                case UPDATE_PRODUCT_STATUS:
                    pmsEventUpdateService.updateProductStatus(newProduct, productSku).subscribe();
                    break;

                case REMOVE_PRODUCT_SKU:
                    pmsEventUpdateService.removeProductSku(productSku).subscribe();
                    break;

                case REMOVE_PRODUCT:
                    pmsEventUpdateService.removeProduct(newProduct).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_PRODUCT, NEW_PRODUCT_SKU, UPDATE_PRODUCT, " +
                            " REMOVE_PRODUCT_SKU and REMOVE_PRODUCT event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
