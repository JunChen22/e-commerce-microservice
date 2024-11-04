package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ProductSku;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductSkuRepository extends ReactiveCrudRepository<ProductSku, Integer> {
    Mono<ProductSku> findBySkuCode(String skuCode);
}
