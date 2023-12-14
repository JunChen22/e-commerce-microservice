package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuExample;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(OmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public OmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper,
                                @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Transactional
    @ApiOperation("Generated order, increase sku lock stock")
    public Mono<Void> updatePurchase(Map<String, Integer> skuQuantity) {
        return Mono.fromRunnable(() -> {
            Set<String> skuSet = skuQuantity.keySet();
            for (String sku : skuSet) {
                ProductSku skuProduct = getProductSku(sku);

                int currentLockStock = skuProduct.getLockStock();
                int quantityPurchased = skuQuantity.get(sku);
                skuProduct.setLockStock(currentLockStock + quantityPurchased);

                skuMapper.updateByPrimaryKey(skuProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }

    @Transactional
    @ApiOperation("Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock")
    public Mono<Void> updatePurchasePayment(Map<String, Integer> skuQuantity) {
        return Mono.fromRunnable(() -> {
            Set<String> skuSet = skuQuantity.keySet();
            for (String sku : skuSet) {
                ProductSku skuProduct = getProductSku(sku);

                int currentLockStock = skuProduct.getLockStock();
                int currentSkuStock = skuProduct.getStock();
                int quantityPurchased = skuQuantity.get(sku);
                skuProduct.setLockStock(currentLockStock - quantityPurchased);
                skuProduct.setStock(currentSkuStock - quantityPurchased);

                skuMapper.updateByPrimaryKey(skuProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }

    @Transactional
    @ApiOperation("Generated order and success payment and return, increase product stock and sku stock")
    public Mono<Void> updateReturn(Map<String, Integer> skuQuantity) {
        return Mono.fromRunnable(() -> {
            Set<String> skuSet = skuQuantity.keySet();
            for (String sku : skuSet) {
                ProductSku skuProduct = getProductSku(sku);

                int productId = skuProduct.getProductId();
                int currentSkuStock = skuProduct.getStock();
                int quantityReturned = skuQuantity.get(sku);

                skuProduct.setStock(currentSkuStock + quantityReturned);
                skuMapper.updateByPrimaryKey(skuProduct);

                Product foundProduct = productMapper.selectByPrimaryKey(productId);

                int currentStock = foundProduct.getStock();
                foundProduct.setStock(currentStock + quantityReturned);
                productMapper.updateByPrimaryKey(foundProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }

    @Transactional
    @ApiOperation("Generated order and failure payment, decrease sku lock stock")
    public Mono<Void> updateFailPayment(Map<String, Integer> skuQuantity) {
        return Mono.fromRunnable(() -> {
            Set<String> skuSet = skuQuantity.keySet();
            for (String sku : skuSet) {
                ProductSku skuProduct = getProductSku(sku);

                int currentLockStock = skuProduct.getLockStock();
                int quantityFreed = skuQuantity.get(sku);
                skuProduct.setLockStock(currentLockStock - quantityFreed);

                skuMapper.updateByPrimaryKey(skuProduct);
            }
        }).subscribeOn(jdbcScheduler).then();
    }

    private ProductSku getProductSku(String skuCode) {
        ProductSkuExample skuExample = new ProductSkuExample();
        skuExample.createCriteria().andSkuCodeEqualTo(skuCode);
        List<ProductSku> skuList = skuMapper.selectByExample(skuExample);
        if (skuList.isEmpty()) throw new RuntimeException("Unable to find product with SKU: " + skuCode);
        return skuList.get(0);
    }
}
