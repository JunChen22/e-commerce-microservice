package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.service.SMS.impl.CouponHistoryServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/coupon")
@PreAuthorize("hasRole('ROLE_admin_sale')")
@Tag(name = "Coupon related", description = "CRUD coupon by admin with right roles/permission")
public class CouponHistoryController {

    private final CouponHistoryServiceImpl historyService;

    @Autowired
    public CouponHistoryController(CouponHistoryServiceImpl historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/listAllUsedCoupon")
    @ApiOperation(value = "return all the coupon used between two time")
    public Flux<UsedCouponHistory> couponUsed() {
        return historyService.couponUsed();
    };

    @GetMapping("/getUserCoupon/{userId}")
    @ApiOperation(value = "shows how many coupon(amount saved) a user used")
    public Flux<CouponHistory> getUserCoupon(@PathVariable int userId) {
        return historyService.getUserCoupon(userId);
    }
}
