package com.itsthatjun.ecommerce.dto.ums.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MemberLoginLogDTO {

    private Date loginTime;

    private String ipAddress;

    private String loginType;
}
