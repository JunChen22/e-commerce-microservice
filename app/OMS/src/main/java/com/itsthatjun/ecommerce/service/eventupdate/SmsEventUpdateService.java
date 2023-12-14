package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public SmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper,
                                 @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Transactional
    @ApiOperation("Update sale price, logic is done already in SMS")
    public Mono<Void> updateSale(List<ProductSku> productSkuList) {
        return Mono.fromRunnable(() -> {
            for (ProductSku sku : productSkuList) {
                skuMapper.updateByPrimaryKey(sku);
                int productId = sku.getProductId();
                Product affectedProduct = productMapper.selectByPrimaryKey(productId);

                double currentSalePrice = affectedProduct.getSalePrice().doubleValue();
                double skuSalePrice = sku.getPromotionPrice().doubleValue();

                affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentSalePrice, skuSalePrice)));
                affectedProduct.setOnSaleStatus(1);
                productMapper.updateByPrimaryKeySelective(affectedProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }

    @Transactional
    @ApiOperation("Remove sale price and update product")
    public Mono<Void> removeSale(List<ProductSku> productSkuList) {
        return Mono.fromRunnable(() -> {
            for (ProductSku sku : productSkuList) {
                skuMapper.updateByPrimaryKey(sku);

                int productId = sku.getProductId();
                Product affectedProduct = productMapper.selectByPrimaryKey(productId);

                affectedProduct.setSalePrice(affectedProduct.getOriginalPrice());
                affectedProduct.setOnSaleStatus(0);

                productMapper.updateByPrimaryKeySelective(affectedProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }
}
