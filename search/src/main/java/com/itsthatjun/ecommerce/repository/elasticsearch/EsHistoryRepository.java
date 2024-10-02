package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.document.elasticsearch.EsHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsHistoryRepository extends ElasticsearchRepository<EsHistory, Long> {

}
