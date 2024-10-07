package com.itsthatjun.ecommerce.document.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document(indexName = "products")
public class EsProduct {

    @Id
    private String id; // ES alphanumeric id

    @Field(type = FieldType.Keyword)
    private String productSn;

    private Long brandId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String productCategoryName;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String subTitle;

    @Field(type = FieldType.Keyword)
    private String keywords;

    private BigDecimal price;

    private Integer sale;

    private Integer newStatus;

    private Integer recommendStatus;

    private Integer stock;

    private Integer promotionType;

    //@Field(type = FieldType.Nested) TODO: will be sku with attributes
    //private List<EsProductAttribute> attrValueList;
}
