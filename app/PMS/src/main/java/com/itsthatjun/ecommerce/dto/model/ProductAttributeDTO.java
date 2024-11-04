package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductAttributeDTO {
    // attribute name to value+unit. 'storage capacity' : '128 GB'.  [attributeName : att_value + " " + att_unit]
    private Map<String, String> attribute;
}
