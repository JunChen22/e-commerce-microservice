package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.model.entity.ProductSku;
import com.itsthatjun.ecommerce.repository.ProductRepository;
import com.itsthatjun.ecommerce.repository.as.ProductSkuRepository;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsEventUpdateService.class);

    private final ProductRepository productRepository;

    private final ProductSkuRepository skuRepository;

    @Autowired
    public SmsEventUpdateService(ProductRepository productRepository, ProductSkuRepository skuRepository) {
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
    }

    /**
     * Update sale price for product and sku. logic is done already in SMS
     */
    @Transactional
    public Mono<Void> updateSale(List<ProductSku> productSkuList) {
        return Flux.fromIterable(productSkuList)
                .flatMap(sku -> {
                    // Update the SKU reactively
                    return skuRepository.save(sku)
                            .then(
                                    // Fetch the associated product reactively by productId
                                    productRepository.findById(sku.getProductId())
                                            .flatMap(affectedProduct -> {
                                                // Compare and set the minimum sale price between the SKU and the Product
                                                BigDecimal currentSalePrice = affectedProduct.getSalePrice();
                                                BigDecimal skuSalePrice = sku.getPromotionPrice();
                                                affectedProduct.setSalePrice(currentSalePrice.min(skuSalePrice));
                                                affectedProduct.setOnSaleStatus(1);

                                                // Save the updated product reactively
                                                return productRepository.save(affectedProduct);
                                            })
                            );
                }).then();
    }

    /**
     * Remove sale price and update product and sku
     */
    @Transactional
    public Mono<Void> removeSale(List<ProductSku> productSkuList) {
        return Flux.fromIterable(productSkuList)
                .flatMap(sku -> {
                    // Save SKU reactively
                    return skuRepository.save(sku)
                            .then(
                                    // Fetch the associated Product by productId reactively
                                    productRepository.findById(sku.getProductId())
                                            .flatMap(affectedProduct -> {
                                                // Reset sale price and on-sale status
                                                affectedProduct.setSalePrice(affectedProduct.getOriginalPrice());
                                                affectedProduct.setOnSaleStatus(0);

                                                // Save updated product reactively
                                                return productRepository.save(affectedProduct);
                                            })
                            );
                }).then();
    }
}
