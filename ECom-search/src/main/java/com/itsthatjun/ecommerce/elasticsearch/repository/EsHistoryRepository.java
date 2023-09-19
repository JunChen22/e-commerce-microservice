package com.itsthatjun.ecommerce.elasticsearch.repository;

import com.itsthatjun.ecommerce.elasticsearch.document.EsHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsHistoryRepository extends ElasticsearchRepository<EsHistory, String> {

}
