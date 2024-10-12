package com.itsthatjun.ecommerce.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("member_login_log")
public class MemberLoginLog {
    @Id
    private Integer id;

    private Integer memberId;

    private Date loginTime;

    private String ipAddress;

    private String loginType;
}