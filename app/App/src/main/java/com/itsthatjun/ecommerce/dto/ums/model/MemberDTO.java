package com.itsthatjun.ecommerce.dto.ums.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
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
