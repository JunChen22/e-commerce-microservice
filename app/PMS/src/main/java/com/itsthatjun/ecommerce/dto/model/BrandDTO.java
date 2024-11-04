package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BrandDTO implements Serializable {

    private String name;

    private String slug;

    private String logo;
}
