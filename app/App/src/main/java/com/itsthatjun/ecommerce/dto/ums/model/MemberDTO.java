package com.itsthatjun.ecommerce.dto.ums.model;

import lombok.Data;

import java.util.Date;

@Data
public class MemberDTO {

    private String username;

    private String password;

    private String name;

    private String phoneNumber;

    private String email;

    private Integer emailSubscription;

    private Integer status;

    private Date createdAt;

    private Date lastLogin;
}
