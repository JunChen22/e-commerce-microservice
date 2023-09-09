package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final ProductMapper productMapper;
    private final CouponHistoryMapper couponHistoryMapper;
    private final CouponProductRelationMapper productRelationMapper;
    private final ProductSkuStockMapper skuStockMapper;

    @Autowired
    public CouponServiceImpl(CouponMapper couponMapper, ProductMapper productMapper, CouponHistoryMapper couponHistoryMapper,
                             CouponProductRelationMapper productRelationMapper, ProductSkuStockMapper skuStockMapper) {
        this.couponMapper = couponMapper;
        this.productMapper = productMapper;
        this.couponHistoryMapper = couponHistoryMapper;
        this.productRelationMapper = productRelationMapper;
        this.skuStockMapper = skuStockMapper;
    }

    @Override
    public Coupon checkCoupon(String couponCode) {
        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andCodeEqualTo(couponCode);
        List<Coupon> result = couponMapper.selectByExample(couponExample);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }


    // -- discount on 0-> all, 1 -> specific brand, 2-> specific item
    @Override
    public List<Coupon> getCouponForProduct(int productId) {
         //TODO: change this to loose coupling
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            throw new ProductException("Can't find product : " + productId);

        String productName = product.getName();
        String brandName = product.getBrandName();

        CouponExample example = new CouponExample();
        // coupon that have product name, brand name, or all product in name
        example.createCriteria().andNameLike("%" + productName + "%");
        example.or().andNameLike("%" + brandName + "%");
        example.or().andNameLike("%" + "all product" + "%");
        example.or().andCouponTypeEqualTo(0);
        return couponMapper.selectByExample(example);
    }

    @Override
    public void updateUsedCoupon(String code, int orderId, int memberId) {
        if (checkCoupon(code) == null) {
            return ;
        }

        // find and update coupon count
        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andCodeEqualTo(code);
        Coupon usedCoupon = couponMapper.selectByExample(couponExample).get(0);
        int usedCount = usedCoupon.getUsedCount() + 1;
        usedCoupon.setUsedCount(usedCount);

        couponMapper.updateByPrimaryKey(usedCoupon);

        // add used history
        CouponHistory couponHistory = new CouponHistory();
        couponHistory.setOrderId(orderId);
        couponHistory.setCouponId(usedCoupon.getId());
        couponHistory.setCode(code);
        couponHistory.setMemberId(memberId);

        couponHistoryMapper.insert(couponHistory);
    }

    @Override
    public double getDiscountAmount(Map<String, Integer> skuQuantity, String couponCode) {

        if (couponCode == null) return 0;

        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andCodeEqualTo(couponCode);
        Coupon coupon = couponMapper.selectByExample(couponExample).get(0);
        if (coupon == null) return 0;

        double totalDiscount = 0;

        // check expiration
        Date startDate = coupon.getStartTime();
        Date endDate = coupon.getEndTime();
        Date currentDate = new Date();

        if (!(currentDate.after(startDate) && currentDate.before(endDate))) {
            return 0;
        }

        // check usage limit
        if (coupon.getCount() <= coupon.getUsedCount() && coupon.getUsedCount() >= coupon.getPublishCount()) return 0;

        // TODO:  create dao and use SQL for simpler method
        // Find products affected by this coupon
        CouponProductRelationExample productRelationExample = new CouponProductRelationExample();
        productRelationExample.createCriteria().andCouponIdEqualTo(coupon.getId());
        List<CouponProductRelation> itemAffectedByCouponList = productRelationMapper.selectByExample(productRelationExample);

        for (String skuCode : skuQuantity.keySet()) {
            String itemSKuCode = skuCode;
            int quantityNeeded = skuQuantity.get(skuCode);

            ProductSkuStockExample productSkuStockExample = new ProductSkuStockExample();
            productSkuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock productSkuStock = skuStockMapper.selectByExample(productSkuStockExample).get(0);

            int itemId = productSkuStock.getProductId();

            for (CouponProductRelation discountItem: itemAffectedByCouponList) {
                if (discountItem.getProductSkuCode().equals(itemSKuCode)  && discountItem.getProductId() == itemId) {
                    if (coupon.getDiscountType() == 0) {// discount by amount
                        totalDiscount = totalDiscount + (coupon.getAmount().doubleValue() * quantityNeeded);
                    } else {   // discount  by percent
                        totalDiscount = totalDiscount + ((coupon.getAmount().doubleValue() * productSkuStock.getPromotionPrice().doubleValue()) / 100) * quantityNeeded;
                    }
                }
            }
        }
        return totalDiscount;
    }
}
