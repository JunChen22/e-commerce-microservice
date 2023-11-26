package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.outgoing.OrderDTO;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDao {

    @ApiModelProperty(value = "Get the order detail by order ID")
    OrderDetail getDetail(@Param("orderSn") String orderSn, @Param("userId") int userId);

    @ApiModelProperty(value = "Get all orders made by user")
    List<OrderDTO> getAllOrders(@Param("userId") int userId);
}