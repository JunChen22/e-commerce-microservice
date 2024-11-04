package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Email;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends ReactiveCrudRepository<Email, Integer> {
}
