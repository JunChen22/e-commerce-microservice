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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@Service
public class PmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(PmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public PmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper,
                            @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @ApiOperation("Add product to local database, only need price, sale price, stock, lock stock, sku and id.")
    public Mono<Void> addProduct(Product newProduct, List<ProductSku> newSkuList) {
        return Mono.fromRunnable(() -> {
                    productMapper.insert(newProduct);

                    for (ProductSku sku : newSkuList) skuMapper.insert(sku);

                    int addedProductId = newProduct.getId();
                    int skuSize = newSkuList.size();
                    LOG.info("Added product with id: %d, and %d product sku", addedProductId, skuSize);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Add a sku to product")
    public Mono<Void> addProductSku(ProductSku newSku) {
        return Mono.fromRunnable(() -> {
                skuMapper.insert(newSku);
                int productId = newSku.getProductId();
                int skuId = newSku.getId();
                String skuCode = newSku.getSkuCode();
                LOG.info("Added sku to product id: %d, with sku id : %d and sku code: %s", productId, skuId, skuCode);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Update product info and sku like stock, original price, online status.")
    public Mono<Void> updateProduct(Product affectedProduct, List<ProductSku> skuList) {
        return Mono.fromRunnable(() -> {
                productMapper.updateByPrimaryKey(affectedProduct);
                for (ProductSku sku : skuList) {
                    skuMapper.updateByPrimaryKey(sku);
                }
                int addedProductId = affectedProduct.getId();
                int skuSize = skuList.size();
                LOG.info("Updated product with id: %d, and %d product sku", addedProductId, skuSize);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Remove a sku from product")
    public Mono<Void> removeProductSku(ProductSku skuToRemove) {
        return Mono.fromRunnable(() -> {
                int productId = skuToRemove.getProductId();
                int deleteSkuId = skuToRemove.getId();
                skuMapper.deleteByPrimaryKey(deleteSkuId);
                LOG.info("Delete product sku with id: %d, and %d product id", deleteSkuId, productId);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Remove product with all of its SKU")
    public Mono<Void> removeProduct(Product product, List<ProductSku> skuList) {
        return Mono.fromRunnable(() -> {
                int productId = product.getId();
                int skuListSize = skuList.size();

                for (ProductSku sku : skuList) {
                    int skuId = sku.getId();
                    skuMapper.deleteByPrimaryKey(skuId);
                }

                productMapper.deleteByPrimaryKey(productId);
                LOG.info("Delete product with id: %d, and %d product sku.", productId, skuListSize);
        }).subscribeOn(jdbcScheduler).then();
    }
}
