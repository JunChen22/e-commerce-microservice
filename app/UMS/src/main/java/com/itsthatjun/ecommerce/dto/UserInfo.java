package com.itsthatjun.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserInfo implements Serializable {

    private String name;

    private String email;
}
