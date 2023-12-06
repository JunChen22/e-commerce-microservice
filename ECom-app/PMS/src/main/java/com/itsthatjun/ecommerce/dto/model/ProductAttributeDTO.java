package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProductAttributeDTO {
    Map<String, String> attribute;      // attribute name to value+unit. 'storage capacity' : '128 GB'.  [attributeName : att_value + " " + att_unit]
}
