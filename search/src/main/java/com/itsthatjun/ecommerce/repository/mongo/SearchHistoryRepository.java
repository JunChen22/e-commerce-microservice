package com.itsthatjun.ecommerce.repository.mongo;

import com.itsthatjun.ecommerce.document.mongo.SearchHistoryDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends ReactiveCrudRepository<SearchHistoryDocument, Long> {

}
