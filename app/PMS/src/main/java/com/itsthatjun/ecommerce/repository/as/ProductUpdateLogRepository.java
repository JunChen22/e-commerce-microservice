package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ProductUpdateLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUpdateLogRepository extends ReactiveCrudRepository<ProductUpdateLog, Integer> {
}
