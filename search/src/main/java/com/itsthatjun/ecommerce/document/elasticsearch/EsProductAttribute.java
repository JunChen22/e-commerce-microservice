package com.itsthatjun.ecommerce.document.elasticsearch;

import lombok.Data;

@Data
public class EsProductAttribute {

    private Long id;

    private Long productAttributeId;

    private String name;

    private String type;

    private String value;
}
