package com.itsthatjun.ecommerce.repository.as;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewUpdateLogRepository extends ReactiveCrudRepository<ReviewUpdateLogRepository, Integer> {
}
