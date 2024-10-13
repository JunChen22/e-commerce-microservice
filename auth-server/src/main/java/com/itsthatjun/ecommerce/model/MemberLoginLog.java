package com.itsthatjun.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@Setter
@Table("member_login_log")
public class MemberLoginLog {
    @Id
    private Integer id;

    private Integer memberId;

    private Date loginTime;

    private String ipAddress;

    private String loginType;
}