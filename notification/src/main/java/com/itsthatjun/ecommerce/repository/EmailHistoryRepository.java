package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.Email;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository extends ReactiveCrudRepository<Email, Integer> {

}