package com.itsthatjun.ecommerce.dto.pms.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProductAttributeDTO {
    Map<String, String> attribute;      // attribute name to value+unit. 'storage capacity' : '128 GB'
}
