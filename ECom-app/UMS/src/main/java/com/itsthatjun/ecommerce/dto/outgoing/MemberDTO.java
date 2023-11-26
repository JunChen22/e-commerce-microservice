package com.itsthatjun.ecommerce.dto.outgoing;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MemberDTO implements Serializable {

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
