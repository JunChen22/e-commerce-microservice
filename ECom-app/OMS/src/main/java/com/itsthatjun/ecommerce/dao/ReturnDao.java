package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

public interface ReturnDao {

    @ApiModelProperty(value = "Get the order detail by order ID")
    ReturnDetail getReturnDetail(@Param("orderSn") String orderSn, @Param("userId") int userId);

    @ApiModelProperty(value = "Get the order detail by order ID")
    ReturnDetail getReturnForNotification(@Param("orderSn") String orderSn);

}
