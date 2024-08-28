package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.AdminLoginLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AdminDTO {
    private Long id;

    private String username;

    private String email;

    private List<String> roles;

    private List<String> permissions;

    private List<AdminLoginLog> loginLogs;
}