package com.itsthatjun.ecommerce.mbg.model;

import java.io.Serializable;

public class ProductAttributeCategory implements Serializable {
    private Integer id;

    private String name;

    private Integer attributeAmount;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAttributeAmount() {
        return attributeAmount;
    }

    public void setAttributeAmount(Integer attributeAmount) {
        this.attributeAmount = attributeAmount;
    }
}