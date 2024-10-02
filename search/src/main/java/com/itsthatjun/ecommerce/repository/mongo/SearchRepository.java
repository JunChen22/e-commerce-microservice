package com.itsthatjun.ecommerce.mongo.repository;


import com.itsthatjun.ecommerce.document.mongo.SearchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends MongoRepository<SearchDocument, String> {

}
