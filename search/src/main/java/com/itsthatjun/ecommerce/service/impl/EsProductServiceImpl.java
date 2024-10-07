package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.repository.elasticsearch.EsProductRepository;
import com.itsthatjun.ecommerce.service.EsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static java.util.logging.Level.FINE;

@Service
public class EsProductServiceImpl implements EsProductService {

    private static final Logger LOG = LoggerFactory.getLogger(EsProductServiceImpl.class);

    private EsProductRepository productRepository;

    @Autowired
    public EsProductServiceImpl(EsProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Flux<EsProduct> search(String keyword, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return productRepository.findByNameContainingOrSubTitleContainingOrKeywordsContaining(keyword, keyword, keyword, pageable)
                .log(LOG.getName(), FINE)
                .onErrorResume(e -> {
                    LOG.error("Failed to search products", e);
                    return Flux.empty();
                });
    }
}
