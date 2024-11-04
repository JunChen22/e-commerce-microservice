package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.EmailTemplates;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EmailTemplatesRepository extends ReactiveCrudRepository<EmailTemplates, Integer> {

    /**
     * find email template based on service name
     * @param serviceName service name like OMS or PMS. order and product.
     * @return pre-defined template with placeholder
     */
    Mono<EmailTemplates> findByServiceName(String serviceName);
}
