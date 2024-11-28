package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleAnalytic;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleAnalyticRepository extends ReactiveCrudRepository<ArticleAnalytic, Integer> {
}