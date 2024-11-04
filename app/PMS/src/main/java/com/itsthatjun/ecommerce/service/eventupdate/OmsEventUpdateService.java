package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.repository.ProductRepository;
import com.itsthatjun.ecommerce.repository.as.ProductSkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(OmsEventUpdateService.class);

    private final ProductRepository productRepository;

    private final ProductSkuRepository skuRepository;

    @Autowired
    public OmsEventUpdateService(ProductRepository productRepository, ProductSkuRepository skuRepository) {
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
    }

    /**
     * Generated order, increase sku lock stock
     */
    @Transactional
    public Mono<Void> updatePurchase(Map<String, Integer> skuQuantity) {
        return Flux.fromIterable(skuQuantity.entrySet())
                .flatMap(entry -> {
                    String sku = entry.getKey();
                    int updatedQuantity = entry.getValue();

                    return skuRepository.findBySkuCode(sku)
                            .map(skuProduct -> {
                                int currentLockStock = skuProduct.getLockStock();
                                skuProduct.setLockStock(currentLockStock + updatedQuantity);
                                return skuProduct;
                            })
                            .flatMap(skuRepository::save)
                            .doOnError(error -> LOG.error("Failed to update lock stock for SKU: {}", sku, error));
                }).then();
    }

    /**
     * Generated order and success payment, decrease product stock, sku stock and sku lock stock
     */
    @Transactional
    public Mono<Void> updatePurchasePayment(Map<String, Integer> skuQuantity) {
        return Flux.fromIterable(skuQuantity.entrySet())
                .flatMap(entry -> {
                    String sku = entry.getKey();
                    int updatedQuantity = entry.getValue();

                    // Decrease sku stock and lock stock
                    return skuRepository.findBySkuCode(sku)
                            .flatMap(skuProduct -> {
                                int currentLockStock = skuProduct.getLockStock();
                                int currentSkuStock = skuProduct.getStock();
                                skuProduct.setLockStock(currentLockStock - updatedQuantity);
                                skuProduct.setStock(currentSkuStock - updatedQuantity);

                                return skuRepository.save(skuProduct)
                                        // Decrease product stock
                                        .then(productRepository.findById(skuProduct.getProductId()))
                                        .flatMap(product -> {
                                            int currentStock = product.getStock();
                                            product.setStock(currentStock - updatedQuantity);
                                            return productRepository.save(product);
                                        });
                            })
                            .doOnError(error -> LOG.error("Failed to update stock for SKU: {}", sku, error));
                }).then();
    }

    /**
     * Generated order and failure payment, decrease sku lock stock
     */
    @Transactional
    public Mono<Void> updateFailPayment(Map<String, Integer> skuQuantity) {
        return Flux.fromIterable(skuQuantity.entrySet())
                .flatMap(entry -> {
                    String sku = entry.getKey();
                    int updatedQuantity = entry.getValue();

                    return skuRepository.findBySkuCode(sku)
                            .map(skuProduct -> {
                                int currentLockStock = skuProduct.getLockStock();
                                skuProduct.setLockStock(currentLockStock - updatedQuantity);
                                return skuProduct;
                            })
                            .flatMap(skuRepository::save)
                            .doOnError(error -> LOG.error("Failed to decrease lock stock for SKU: {}", sku, error));
                }).then();
    }

    /**
     * Generated order and success payment and return, increase product stock and sku stock
     */
    @Transactional
    public Mono<Void> updateReturn(Map<String, Integer> skuQuantity) {
        return Flux.fromIterable(skuQuantity.entrySet())
                .flatMap(entry -> {
                    String sku = entry.getKey();
                    int updatedQuantity = entry.getValue();

                    return skuRepository.findBySkuCode(sku)
                            .flatMap(skuProduct -> {
                                int currentSkuStock = skuProduct.getStock();
                                skuProduct.setStock(currentSkuStock + updatedQuantity);

                                return skuRepository.save(skuProduct)
                                        // Increase product stock
                                        .then(productRepository.findById(skuProduct.getProductId()))
                                        .flatMap(product -> {
                                            int currentStock = product.getStock();
                                            product.setStock(currentStock + updatedQuantity);
                                            return productRepository.save(product);
                                        });
                            }).doOnError(error -> LOG.error("Failed to increase stock for SKU: {}", sku, error));
                }).then();
    }
}
