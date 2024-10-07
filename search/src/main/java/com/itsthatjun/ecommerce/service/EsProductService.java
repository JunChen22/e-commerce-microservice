package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import reactor.core.publisher.Flux;

public interface EsProductService {

    Flux<EsProduct> search(String keyword, int pageNum, int pageSize);

    //Flux<EsProduct> search(String name, String keyword, int category, int pageNum, int pageSize, int sort);

    // TODO: recommendation

    // EsProductRelatedInfo searchRelatedInfo(String keyword);
}
