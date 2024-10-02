package com.itsthatjun.ecommerce.elasticsearch.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "search-history")
public class EsHistory {

    @Id
    private Long id;
    private String searchTerm;
    private String timestamp;
    private Integer userId;
}
