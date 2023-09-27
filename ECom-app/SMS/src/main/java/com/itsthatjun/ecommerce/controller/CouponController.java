package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.CouponDiscount;
import com.itsthatjun.ecommerce.dto.CouponSale;
import com.itsthatjun.ecommerce.mbg.mapper.CouponMapper;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public double checkCoupon(@RequestParam String couponCode, @RequestHeader("X-UserId") int userId) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount
        System.out.println("at coupon controller");
        return couponService.checkDiscount(couponCode, userId);
    }

    @GetMapping("/admin/listAll")
    @ApiOperation(value = "return all working non-expired coupon")
    public List<Coupon> listAll() {
        // TODO: add default value to get only active or disabled coupon , currently is all
        List<Coupon> couponList = couponService.getAllCoupon();
        return couponList;
    }

    @GetMapping("/admin/{couponId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Coupon list(@PathVariable int couponId) {
        return couponService.getACoupon(couponId);
    }

    @GetMapping("/admin/product/all/{productId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public List<Coupon> getCouponForProduct(@PathVariable int productId) {
        List<Coupon> couponList = couponService.getCouponForProduct(productId);
        return couponList;
    }

    @PostMapping("/admin/create")
    @ApiOperation(value = "create a coupon")
    public Coupon create(@RequestBody CouponSale newCouponSale) {
        Coupon newCoupon = newCouponSale.getCoupon();
        Map<String, Integer> affectProduct = newCouponSale.getSkuQuantity();
        couponService.updateCoupon(newCoupon, affectProduct);
        return newCoupon;
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "update a coupon")
    public Coupon update(@RequestBody CouponSale newCouponSale){
        Coupon updatedCoupon = newCouponSale.getCoupon();
        Map<String, Integer> affectProduct = newCouponSale.getSkuQuantity();
        couponService.updateCoupon(updatedCoupon, affectProduct);
        return updatedCoupon;
    }

    @DeleteMapping("/admin/delete/{couponId}")
    @ApiOperation(value = "delete a coupon")
    public void delete(@PathVariable int couponId) {
        couponService.deleteCoupon(couponId);
    }
}
