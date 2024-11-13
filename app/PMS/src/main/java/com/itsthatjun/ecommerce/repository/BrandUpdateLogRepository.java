package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.BrandUpdateLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandUpdateLogRepository extends ReactiveCrudRepository<BrandUpdateLog, Integer> {
}
