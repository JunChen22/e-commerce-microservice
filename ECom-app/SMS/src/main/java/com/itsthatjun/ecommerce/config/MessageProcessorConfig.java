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

import java.util.List;
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
            Map<String, Integer> skuQuantity = event.getCouponSale().getSkuQuantity();;
            switch (event.getEventType()) {

                case CREATE_COUPON:
                    couponService.createCoupon(coupon, skuQuantity);
                    break;

                case UPDATE_COUPON:
                    couponService.updateCoupon(coupon, skuQuantity);
                    break;

                case DELETE_COUPON:
                    int couponId = event.getCouponSale().getCouponId();
                    couponService.deleteCoupon(couponId);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_COUPON, " +
                            "UPDATE_COUPON, and DELETE_COUPON event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
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

                case CREATE_SALE:
                    salesService.createListSale(saleRequest, operator);
                    break;

                case UPDATE_SALE_INFO:
                    salesService.updateSaleInfo(saleRequest, operator);
                    break;

                case UPDATE_SALE_PRICE:
                    salesService.updateSalePrice(saleRequest, operator);
                    break;

                case UPDATE_SALE_STATUS:
                    salesService.updateSaleStatus(saleRequest, operator);
                    break;

                case DELETE_SALE:
                    int promotionId = saleRequest.getPromotionSale().getId();
                    salesService.delete(promotionId, operator);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE_SALE, " +
                            "UPDATE_SALE_INFO, UPDATE_SALE_PRICE, UPDATE_SALE_STATUS and DELETE_SALE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }

    @Bean
    public Consumer<OmsCouponUpdateIncomingEvent> couponMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            if(event.getEventType() == UPDATE_COUPON_USAGE) {
                couponService.updateUsedCoupon(event.getCoupon(), event.getOrderId(), event.getMemberId());
            } else {
                String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected UPDATE_COUPON_USAGE event";
                LOG.warn(errorMessage);
                throw new RuntimeException(errorMessage); // TODO: create event exception
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
    public Consumer<PmsProductUpdateIncomingEvent> productStockMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Product product = event.getProduct();
            List<ProductSku> productSkuList = event.getProductSkuList();
            switch (event.getEventType()) {

                case NEW_PRODUCT:
                    pmsEventUpdateService.addProduct(product, productSkuList);
                    break;

                case NEW_PRODUCT_SKU:
                    pmsEventUpdateService.addProductSku(productSkuList.get(0));
                    break;

                case UPDATE_PRODUCT:
                    pmsEventUpdateService.updateProduct(product, productSkuList);
                    break;

                case REMOVE_PRODUCT:
                    pmsEventUpdateService.removeProductSku(productSkuList.get(0));
                    break;

                case REMOVE_PRODUCT_SKU:
                    pmsEventUpdateService.removeProduct(product, productSkuList);
                    break;

                default:

                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected NEW_PRODUCT, " +
                            "NEW_PRODUCT_SKU, UPDATE_PRODUCT, REMOVE_PRODUCT and REMOVE_PRODUCT_SKU event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
