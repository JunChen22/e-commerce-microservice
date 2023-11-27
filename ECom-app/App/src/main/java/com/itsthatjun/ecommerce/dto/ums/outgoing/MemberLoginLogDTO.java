package com.itsthatjun.ecommerce.dto.ums.outgoing;

import lombok.Data;

import java.util.Date;

@Data
public class MemberLoginLogDTO {

    private Date loginTime;

    private String ipAddress;

    private String loginType;
}
