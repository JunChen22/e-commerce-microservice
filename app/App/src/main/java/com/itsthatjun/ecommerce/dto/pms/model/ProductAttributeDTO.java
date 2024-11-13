package com.itsthatjun.ecommerce.dto.pms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductAttributeDTO {
    /**
     * attribute name to value+unit. 'storage capacity' : '128 GB'
     */
    Map<String, String> attribute;
}
