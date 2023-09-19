package com.itsthatjun.ecommerce.elasticsearch.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "searchHistory")
public class EsHistory {

    @Id
    private String id;
    private String searchTerm;
    private String timestamp;
    private Integer userId;
}
