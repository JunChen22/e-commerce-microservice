package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.controller.CMS.ContentAggregate;
import com.itsthatjun.ecommerce.controller.OMS.CartAggregate;
import com.itsthatjun.ecommerce.controller.OMS.OrderAggregate;
import com.itsthatjun.ecommerce.controller.OMS.ReturnAggregate;
import com.itsthatjun.ecommerce.controller.PMS.BrandAggregate;
import com.itsthatjun.ecommerce.controller.PMS.ProductAggregate;
import com.itsthatjun.ecommerce.controller.PMS.ReviewAggregate;
import com.itsthatjun.ecommerce.controller.SMS.CouponAggregate;
import com.itsthatjun.ecommerce.controller.SMS.SaleAggregate;
import com.itsthatjun.ecommerce.controller.UMS.UserAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

public class HealthCheckConfiguration {

    @Autowired
    private ReturnAggregate returnAggregate;

    @Autowired
    private ContentAggregate contentAggregate;

    @Autowired
    private CartAggregate cartAggregate;

    @Autowired
    private OrderAggregate orderAggregate;

    @Autowired
    private BrandAggregate brandAggregate;

    @Autowired
    private ProductAggregate productAggregate;

    @Autowired
    private ReviewAggregate reviewAggregate;

    @Autowired
    private CouponAggregate couponAggregate;

    @Autowired
    private SaleAggregate saleAggregate;

    @Autowired
    private UserAggregate userAggregate;

    @Bean
    ReactiveHealthContributor coreService() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();

        registry.put("cms-article", () -> contentAggregate.getCmsHealth());

        registry.put("oms-cart", () -> cartAggregate.getOmsHealth());
        registry.put("oms-order", () -> orderAggregate.getOmsHealth());
        registry.put("oms-return", () -> returnAggregate.getOmsHealth());

        registry.put("pms-brand", () -> brandAggregate.getPmsHealth());
        registry.put("pms-product", () -> productAggregate.getPmsHealth());
        registry.put("pms-review", () -> reviewAggregate.getPmsHealth());

        registry.put("sms-coupon", () -> couponAggregate.getSmsHealth());
        registry.put("sms-sale", () -> saleAggregate.getSmsHealth());

        registry.put("ums-user", () -> userAggregate.getUmsHealth());

        return CompositeReactiveHealthContributor.fromMap(registry);
    }
}
