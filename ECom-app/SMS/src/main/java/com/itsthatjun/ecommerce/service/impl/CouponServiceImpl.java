package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.CouponDiscount;
import com.itsthatjun.ecommerce.exceptions.CouponException;
import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponMapper couponMapper;

    private final ProductMapper productMapper;

    private final CouponHistoryMapper couponHistoryMapper;

    private final CouponChangeLogMapper logMapper;

    private final CouponProductRelationMapper productRelationMapper;

    private final ProductSkuMapper productSkuMapper;

    private final String OMS_SERVICE_URL = "http://oms:8080/cart";

    private final WebClient webClient;

    private final Scheduler jdbcScheduler;

    @Autowired
    public CouponServiceImpl(CouponMapper couponMapper, ProductMapper productMapper, CouponHistoryMapper couponHistoryMapper,
                             CouponProductRelationMapper productRelationMapper, ProductSkuMapper productSkuMapper, CouponChangeLogMapper logMapper,
                             WebClient.Builder webClient, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.couponMapper = couponMapper;
        this.productMapper = productMapper;
        this.couponHistoryMapper = couponHistoryMapper;
        this.productRelationMapper = productRelationMapper;
        this.productSkuMapper = productSkuMapper;
        this.logMapper = logMapper;
        this.webClient = webClient.build();
        this.jdbcScheduler = jdbcScheduler;
    }
    @Override
    public Mono<Double> checkDiscount(String couponCode, int userId) {
        return Mono.fromCallable(() -> {
            Coupon foundCoupon = checkCoupon(couponCode);
            if (foundCoupon == null) return 0.0;

            List<CartItem> cartItemList = getCartFromOms(userId);
            if (cartItemList.isEmpty()) return 0.0;

            Map<String, Integer> skuQuantity = new HashMap<>();
            for (CartItem cartItem : cartItemList) {
                int quantity = cartItem.getQuantity();
                String skuCode = cartItem.getProductSku();
                skuQuantity.put(skuCode, quantity);
            }
            return getDiscountAmount(skuQuantity, foundCoupon);
        }).subscribeOn(jdbcScheduler);
    }

    private List<CartItem> getCartFromOms(int userId) {
        String url = OMS_SERVICE_URL + "/list";
        LOG.debug("Will call the list API on URL: {}", url);

        // Define the timeout in milliseconds
        int timeoutMilliseconds = 200; // 0.2 second
        List<CartItem> cartItems = null;

        try {
            // Make a synchronous HTTP GET request with a timeout
            Mono<List<CartItem>> cartItemListMono = webClient.get()
                    .uri(url)
                    .header("X-UserId", String.valueOf(userId))
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("Server error"))) // Use Mono.error
                    .bodyToFlux(CartItem.class) // Use bodyToFlux for a list of CartItem
                    .timeout(Duration.ofMillis(timeoutMilliseconds))
                    .collectList(); // Collect the items into a list
            cartItems = cartItemListMono.block();
        } catch (Exception e) {
            // Handle errors, including timeouts, here
            cartItems = Collections.emptyList();
        }
        return cartItems;
    }

    private Coupon checkCoupon(String couponCode) {
        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andCodeEqualTo(couponCode);
        List<Coupon> result = couponMapper.selectByExample(couponExample);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    private double getDiscountAmount(Map<String, Integer> skuQuantity, Coupon coupon) {
        double totalDiscount = 0;

        // check expiration
        Date startDate = coupon.getStartTime();
        Date endDate = coupon.getEndTime();
        Date currentDate = new Date();

        if (!(currentDate.after(startDate) && currentDate.before(endDate))) {
            return 0;
        }

        if (coupon.getStatus() == 0) return 0;  // not active coupon

        // check usage limit
        if (coupon.getCount() <= coupon.getUsedCount() && coupon.getUsedCount() >= coupon.getPublishCount()) {
            LOG.info("Coupon expired.");
            return 0;
        }

        // discount on 0-> all, 1 -> specific brand,  2-> specific category , 3-> specific item
        if (coupon.getCouponType() == 0) {  // whole order discount
            totalDiscount = wholeOrderDiscount(skuQuantity, coupon);
            return totalDiscount;
        }

        // TODO:  create dao and use SQL for simpler method
        // Find products affected by this coupon
        CouponProductRelationExample productRelationExample = new CouponProductRelationExample();
        productRelationExample.createCriteria().andCouponIdEqualTo(coupon.getId());
        List<CouponProductRelation> itemAffectedByCouponList = productRelationMapper.selectByExample(productRelationExample);

        for (String skuCode : skuQuantity.keySet()) {
            String itemSKuCode = skuCode;
            int quantityNeeded = skuQuantity.get(skuCode);

            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku productSkuStock = productSkuMapper.selectByExample(skuExample).get(0);

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

    private double wholeOrderDiscount(Map<String, Integer> skuQuantity, Coupon coupon) {
        double totalDiscount = 0;
        double totalPrice = 0;
        for (String skuCode : skuQuantity.keySet()) {
            int quantityNeeded = skuQuantity.get(skuCode);

            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku productSkuStock = productSkuMapper.selectByExample(skuExample).get(0);
            totalPrice += productSkuStock.getPromotionPrice().doubleValue() * quantityNeeded;
        }

        if (coupon.getDiscountType() == 0) {// discount by amount
            totalDiscount = coupon.getAmount().doubleValue();
        } else {   // discount  by percent
            totalDiscount =  ((coupon.getAmount().doubleValue() * totalPrice) / 100);
        }
        return totalDiscount;
    }

    // -- discount on 0-> all, 1 -> specific brand, 2-> specific item
    @Override
    public Flux<Coupon> getCouponForProduct(int productId) {
        return Mono.fromCallable(() -> {
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product == null) throw new ProductException("Can't find product : " + productId);

            String productName = product.getName();
            String brandName = product.getBrandName();

            CouponExample example = new CouponExample();
            // coupon that have product name, brand name, or all product in name
            example.createCriteria().andNameLike("%" + productName + "%");
            example.or().andNameLike("%" + brandName + "%");
            example.or().andNameLike("%" + "all product" + "%");
            example.or().andCouponTypeEqualTo(0);
            return couponMapper.selectByExample(example);
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> updateUsedCoupon(String code, int orderId, int memberId) {
        return Mono.fromRunnable(() -> {
            if (checkCoupon(code) == null) throw new RuntimeException("coupon code is empty can't update");

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
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Flux<Coupon> getAllCoupon() {
        return Mono.fromCallable(() -> {
            LocalDate localDate = LocalDate.now();
            java.sql.Date date = java.sql.Date.valueOf(localDate);
            CouponExample example = new CouponExample();
            example.createCriteria().andEndTimeGreaterThan(date);
            return couponMapper.selectByExample(example);
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Coupon> getACoupon(int id) {
        return Mono.fromCallable(() ->
            couponMapper.selectByPrimaryKey(id)
        ).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Coupon> createCoupon(Coupon newCoupon, Map<String, Integer> skuMap, String operator) {
        // TODO: create create brand or category discount by pass brand name or category id
        return Mono.fromCallable(() -> {
            Coupon coupon = internalCreateCoupon(newCoupon, skuMap, operator);
            return coupon;
        }).subscribeOn(jdbcScheduler);
    }

    private Coupon internalCreateCoupon(Coupon newCoupon, Map<String, Integer> skuMap,  String operator) {
        String couponCode = newCoupon.getCode();
        Coupon existingCoupon = checkCoupon(couponCode);
        if (existingCoupon != null) throw new CouponException("Coupon code already exist");

        int couponType = newCoupon.getCouponType();
        if (couponType == 0) {  // whole order discount coupon, don't need to add affect item
            couponMapper.insert(newCoupon);
            return newCoupon;
        }

        couponMapper.insert(newCoupon);
        int couponId = newCoupon.getId();

        // quantity is not needed, could also store product id into value, <Sku code, productId>
        for (String skuCode : skuMap.keySet()) {
            CouponProductRelation newProduct = new CouponProductRelation();

            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSku productSku = productSkuMapper.selectByExample(productSkuExample).get(0);

            int productId = productSku.getProductId();

            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            String productName= foundProduct.getName();
            String productSn = foundProduct.getSn();

            newProduct.setCouponId(couponId);
            newProduct.setProductId(productId);
            newProduct.setProductName(productName);
            newProduct.setProductSn(productSn);
            newProduct.setProductSkuCode(skuCode);
            productRelationMapper.insert(newProduct);
        }

        createUpdateLog(newCoupon, "create new coupon", operator);

        return newCoupon;
    }

    @Override
    public Mono<Coupon> updateCoupon(Coupon updateCoupon, Map<String, Integer> skuMap, String operator) {
        return Mono.fromCallable(() -> {
            int couponId = updateCoupon.getId();
            Coupon foundCoupon = couponMapper.selectByPrimaryKey(couponId);

            // currently can't change coupon type or coupon code itself
            if (foundCoupon.getCouponType().intValue() != updateCoupon.getCouponType().intValue()) {
                throw new CouponException("Can not change coupon type");
            }

            if (!foundCoupon.getCode().equals(updateCoupon.getCode())) {
                throw new CouponException("Can not change coupon code");
            }

            // TODO: Update coupon info's like discount type, amount, date, count, status, and name
            //      if pricing affecting coupon, will need to update affected product
            couponMapper.updateByPrimaryKeySelective(updateCoupon);

            createUpdateLog(updateCoupon, "update coupon", operator);

            return updateCoupon;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteCoupon(int couponId, String operator) {
        return Mono.fromRunnable(() -> {
            Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
            if (coupon == null) throw new RuntimeException("Coupon not found: " + couponId);

            couponMapper.deleteByPrimaryKey(couponId);

            CouponProductRelationExample productRelationExample = new CouponProductRelationExample();
            productRelationExample.createCriteria().andCouponIdEqualTo(couponId);
            List<CouponProductRelation> couponProductRelationList = productRelationMapper.selectByExample(productRelationExample);

            for (CouponProductRelation product : couponProductRelationList) {
                int productRelationId = product.getId();
                productRelationMapper.deleteByPrimaryKey(productRelationId);
            }

            createUpdateLog(coupon, "delete coupon", operator);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void createUpdateLog(Coupon coupon, String updateAction, String operator) {
        CouponChangeLog couponChangeLog = new CouponChangeLog();
        couponChangeLog.setCouponId(coupon.getId());
        couponChangeLog.setUpdateAction(updateAction);
        couponChangeLog.setOperator(operator);
        couponChangeLog.setCreatedAt(new Date());
        logMapper.insert(couponChangeLog);
    }
}
