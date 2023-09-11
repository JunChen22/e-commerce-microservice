package com.itsthatjun.ecommerce.dto.ums;

import lombok.Data;

@Data
public class RolePermission {

    String roleName;
    String PermissionCategory;
    String Description;
    String value;
}
