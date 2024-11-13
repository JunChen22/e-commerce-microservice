package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ProductAttributeCategory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeCategoryRepository extends ReactiveCrudRepository<ProductAttributeCategory, Integer> {
}
