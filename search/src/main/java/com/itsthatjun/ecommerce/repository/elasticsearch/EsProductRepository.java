package com.itsthatjun.ecommerce.repository.elasticsearch;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EsProductRepository extends ReactiveElasticsearchRepository<EsProduct, String> {

    Flux<EsProduct> findByKeywords(String keyword);
    Flux<EsProduct> findByName(String name);
    Flux<EsProduct> findByNameOrKeywords(String name, String keywords);
    Flux<EsProduct> findByNameContainingOrSubTitleContainingOrKeywordsContaining(String name, String subtitle, String keywords, Pageable page);
    // Elastic Search don't support findAllContaining
    Flux<EsProduct> findAll();
    Flux<EsProduct> findByNameContaining(String name, Sort sort);
    Flux<EsProduct> findByNameContainingOrKeywordsContaining(String name, String keywords);
}
