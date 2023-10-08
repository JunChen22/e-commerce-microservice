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
import reactor.core.publisher.Flux;
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
        Set<String> skuSet = skuQuantity.keySet();
        return Flux.fromIterable(skuSet)
                .flatMap(sku -> blockingUpdateSkuQuantity(sku, skuQuantity))
                .subscribeOn(jdbcScheduler)
                .then();
    }

    private Mono<ProductSku> blockingUpdateSkuQuantity(String sku, Map<String, Integer> skuQuantity) {
        return Mono.fromCallable(() -> {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) {
                throw new RuntimeException("Unable to find product with SKU: " + sku);
            }

            ProductSku skuProduct = skuList.get(0);

            int currentLockStock = skuProduct.getLockStock();
            int quantityPurchased = skuQuantity.get(sku);
            skuProduct.setLockStock(currentLockStock + quantityPurchased);

            skuMapper.updateByPrimaryKey(skuProduct);

            return skuProduct;
        });
    }

    @Transactional
    @ApiOperation("Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock")
    public Mono<Void> updatePurchasePayment(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();
        return Flux.fromIterable(skuSet)
                .flatMap(sku -> Mono.fromCallable(() -> {
                    // Perform the potentially blocking operation within a separate thread
                    return blockingUpdatePurchasePayment(sku, skuQuantity);
                }).subscribeOn(jdbcScheduler))
                .then();
    }

    private ProductSku blockingUpdatePurchasePayment(String sku, Map<String, Integer> skuQuantity) {
        ProductSkuExample skuExample = new ProductSkuExample();
        skuExample.createCriteria().andSkuCodeEqualTo(sku);
        List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

        if (skuList.isEmpty()) {
            throw new RuntimeException("Unable to find product with SKU: " + sku);
        }

        ProductSku skuProduct = skuList.get(0);

        int currentLockStock = skuProduct.getLockStock();
        int quantityPurchased = skuQuantity.get(sku);
        skuProduct.setLockStock(currentLockStock + quantityPurchased);

        skuMapper.updateByPrimaryKey(skuProduct);

        return skuProduct;
    }

    @Transactional
    @ApiOperation("Generated order and success payment and return, increase product stock and sku stock")
    public Mono<Void> updateReturn(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();
        return Flux.fromIterable(skuSet)
                .flatMap(sku -> blockingUpdateReturnQuantity(sku, skuQuantity))
                .subscribeOn(jdbcScheduler)
                .then();
    }

    private Mono<ProductSku> blockingUpdateReturnQuantity(String sku, Map<String, Integer> skuQuantity) {
        return Mono.fromCallable(() -> {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) {
                throw new RuntimeException("Unable to find product with SKU: " + sku);
            }

            ProductSku skuProduct = skuList.get(0);

            int productId = skuProduct.getProductId();
            int currentSkuStock = skuProduct.getStock();
            int quantityReturned = skuQuantity.get(sku);

            skuProduct.setStock(currentSkuStock + quantityReturned);
            skuMapper.updateByPrimaryKey(skuProduct);

            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            int currentStock = foundProduct.getStock();
            foundProduct.setStock(currentStock + quantityReturned);
            productMapper.updateByPrimaryKey(foundProduct);

            return skuProduct;
        });
    }

    @Transactional
    @ApiOperation("Generated order and failure payment, decrease sku lock stock")
    public Mono<Void> updateFailPayment(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();
        return Flux.fromIterable(skuSet)
                .flatMap(sku -> blockUpdateFailedPayment(sku, skuQuantity))
                .subscribeOn(jdbcScheduler)
                .then();
    }

    private Mono<ProductSku> blockUpdateFailedPayment(String sku, Map<String, Integer> skuQuantity) {
        return Mono.fromCallable(() -> {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) {
                throw new RuntimeException("Unable to find product with SKU: " + sku);
            }

            ProductSku skuProduct = skuList.get(0);

            int currentLockStock = skuProduct.getLockStock();
            int quantityFreed = skuQuantity.get(sku);
            skuProduct.setLockStock(currentLockStock - quantityFreed);

            skuMapper.updateByPrimaryKey(skuProduct);
            return skuProduct;
        });
    }
}
