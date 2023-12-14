package com.itsthatjun.ecommerce.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private String name;
    private String email;

}
