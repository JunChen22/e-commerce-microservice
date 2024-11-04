package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Review;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends ReactiveCrudRepository<Review, Integer> {
}
