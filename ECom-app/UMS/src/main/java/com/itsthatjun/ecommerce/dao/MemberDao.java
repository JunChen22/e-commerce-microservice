package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberDao {

    @ApiModelProperty(value = "")
    MemberDetail getUserDetail(@Param("userId") int userId);

    @ApiModelProperty(value = "Get all user with name and email for Notification service")
    List<UserInfo> getAllUserInfo();

    @ApiModelProperty(value = "Get all user with name and email that signed up sale promo for Notification service")
    List<UserInfo> getAllUserInfoSalePromo();
}
