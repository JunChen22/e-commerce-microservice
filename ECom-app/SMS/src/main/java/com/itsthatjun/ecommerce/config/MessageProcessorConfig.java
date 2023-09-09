package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.SmsCouponEvent;
import com.itsthatjun.ecommerce.dto.event.SmsSalesStockEvent;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import com.itsthatjun.ecommerce.service.impl.SalesServiceimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.SmsCouponEvent.Type.UPDATE_COUPON_USAGE;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CouponServiceImpl couponService;

    private final SalesServiceimpl salesService;

    @Autowired
    public MessageProcessorConfig(CouponServiceImpl couponService, SalesServiceimpl salesService) {
        this.couponService = couponService;
        this.salesService = salesService;
    }

    @Bean
    public Consumer<SmsCouponEvent> couponMessageProcessor() {
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
    public Consumer<SmsSalesStockEvent> salesStockMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            Map<String, Integer> skuQuantityMap = event.getProductMap();
            switch (event.getEventType()) {

                case UPDATE_PURCHASE:
                    salesService.updatePurchase(skuQuantityMap);
                    break;

                case UPDATE_PURCHASE_PAYMENT:
                    salesService.updatePurchasePayment(skuQuantityMap);
                    break;

                case UPDATE_RETURN:
                    salesService.updateReturn(skuQuantityMap);
                    break;

                case UPDATE_FAIL_PAYMENT:
                    salesService.updateFailPayment(skuQuantityMap);
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
