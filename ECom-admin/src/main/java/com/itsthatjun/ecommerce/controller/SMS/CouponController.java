package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.SMS.impl.CouponServiceImpl;
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

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/coupon")
@Api(tags = "Coupon related", description = "CRUD coupon by admin with right roles/permission")
public class CouponController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponController.class);

    private final CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "return all working non-expired coupon")
    public Flux<Coupon> listAll() {
        return couponService.listAll();
    }

    @GetMapping("/{couponId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Mono<Coupon> list(@PathVariable int couponId) {
        return couponService.list(couponId);
    }

    @GetMapping("/product/all/{productId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Flux<Coupon> getCouponForProduct(@PathVariable int productId) {
        return couponService.getCouponForProduct(productId);
    }

    @PostMapping("/create")
    @ApiOperation(value = "create a coupon")
    public Mono<CouponSale> create(@RequestBody CouponSale couponSale, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return couponService.create(couponSale, operatorName);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a coupon")
    public Mono<CouponSale> update(@RequestBody CouponSale updatedCouponSale, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return couponService.update(updatedCouponSale, operatorName);
    }

    @DeleteMapping("/delete/{couponId}")
    @ApiOperation(value = "delete a coupon")
    public Mono<Void> delete(@PathVariable int couponId, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return couponService.delete(couponId, operatorName);
    }
}
