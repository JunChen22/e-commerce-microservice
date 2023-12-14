package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.model.OrderDTO;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDao {

    @ApiModelProperty(value = "Get the order detail by order ID")
    OrderDetail getDetail(@Param("orderSn") String orderSn, @Param("userId") int userId);

    @ApiModelProperty(value = "Get the order detail by order ID")
    OrderDetail getOrderForNotification(@Param("orderSn") String orderSn);  // TODO: might need to change this, no useId not good idea

    @ApiModelProperty(value = "Get all orders made by user")
    List<OrderDTO> getUserAllOrders(@Param("userId") int userId);

    List<OrderDetail> getUserPurchasedItem(@Param("productSku") String productSku); // TODO: might add time frame too.
}