package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.EmailTemplates;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EmailTemplatesRepository extends ReactiveCrudRepository<EmailTemplates, Integer> {
    Mono<EmailTemplates> findByServiceName(String serviceName);
}
