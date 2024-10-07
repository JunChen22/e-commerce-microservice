package com.itsthatjun.ecommerce.document.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "searchHistory")
public class SearchHistoryDocument {

    @Id
    private Long id;

    @Version
    private Integer version;

    private String keyword;

    private Date date;
}
