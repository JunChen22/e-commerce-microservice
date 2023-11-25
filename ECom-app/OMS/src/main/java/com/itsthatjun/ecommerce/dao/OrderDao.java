package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

public interface OrderDao {

    @ApiModelProperty(value = "Get the order detail by order ID", example = "12345")
    OrderDetail getDetail(@Param("orderSn") String orderSn, @Param("userId") int userId);
}