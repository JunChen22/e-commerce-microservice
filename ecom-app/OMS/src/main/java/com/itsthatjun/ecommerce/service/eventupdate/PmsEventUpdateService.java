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
    public Mono<Void> addProduct(Product newProduct, ProductSku newSku) {
        return Mono.fromRunnable(() -> {
            productMapper.insert(newProduct);
            skuMapper.insert(newSku);

            int addedProductId = newProduct.getId();
            String SkuCode = newSku.getSkuCode();
            LOG.info("Added product with id: %d, and %s product sku", addedProductId, SkuCode);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Add a sku to product")
    public Mono<Void> addProductSku(Product product, ProductSku newSku) {
        return Mono.fromRunnable(() -> {
            productMapper.updateByPrimaryKey(product);
            skuMapper.insert(newSku);

            int productId = product.getId();
            int skuId = newSku.getId();
            String skuCode = newSku.getSkuCode();
            LOG.info("Added sku to product id: %d, with sku id : %d and sku code: %s", productId, skuId, skuCode);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Update product info and sku like stock and original price.")
    public Mono<Void> updateProduct(Product affectedProduct, ProductSku sku) {
        return Mono.fromRunnable(() -> {
            productMapper.updateByPrimaryKeySelective(affectedProduct);
            skuMapper.updateByPrimaryKeySelective(sku);

            int addedProductId = affectedProduct.getId();
            String skuCode = sku.getSkuCode();
            LOG.info("Updated product with id: %d, and sku code: %s", addedProductId, skuCode);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Update product online status. Product sku will need to turn on one by one.")
    public Mono<Void> updateProductStatus(Product affectedProduct, ProductSku affectedSku) {
        return Mono.fromRunnable(() -> {
            int productId = affectedProduct.getId();
            if (affectedSku == null) {  // all sku update
                ProductSkuExample skuExample = new ProductSkuExample();
                skuExample.createCriteria().andProductIdEqualTo(productId);
                List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

                int status = affectedProduct.getPublishStatus();

                if (status == 0) {
                    for (ProductSku sku : skuList) {
                        sku.setStatus(status);
                        skuMapper.updateByPrimaryKeySelective(sku);
                    }
                }
            } else {    // single sku status update
                skuMapper.updateByPrimaryKeySelective(affectedSku);
            }

            productMapper.updateByPrimaryKeySelective(affectedProduct);
            LOG.info("Updated product with id: %d.", productId);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Remove a sku from product")
    public Mono<Void> removeProductSku(ProductSku skuToRemove) {
        return Mono.fromRunnable(() -> {
            int productId = skuToRemove.getProductId();
            int deleteSkuId = skuToRemove.getId();
            skuMapper.updateByPrimaryKeySelective(skuToRemove); // soft delete
            LOG.info("Delete product sku with id: %d, and %d product id", deleteSkuId, productId);
        }).subscribeOn(jdbcScheduler).then();
    }

    @ApiOperation("Soft delete product with all of its SKU")
    public Mono<Void> removeProduct(Product product) {
        return Mono.fromRunnable(() -> {
            int productId = product.getId();

            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andProductIdEqualTo(productId);
            List<ProductSku> productSkuList = skuMapper.selectByExample(skuExample);
            for (ProductSku sku : productSkuList) {
                sku.setStatus(0);
                skuMapper.updateByPrimaryKeySelective(sku);
            }

            productMapper.updateByPrimaryKeySelective(product);
            LOG.info("Delete product with id: %d, and %d product sku.", productId, productSkuList.size());
        }).subscribeOn(jdbcScheduler).then();
    }
}
