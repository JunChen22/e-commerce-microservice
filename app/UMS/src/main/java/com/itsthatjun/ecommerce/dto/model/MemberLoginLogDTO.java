package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MemberLoginLogDTO implements Serializable {

    private Date loginTime;

    private String ipAddress;

    private String loginType;
}
