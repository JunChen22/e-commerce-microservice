package com.itsthatjun.ecommerce.mbg.model;

public class ProductAttributeCategory {
    private Integer id;

    private String name;

    private Integer attributeAmount;

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