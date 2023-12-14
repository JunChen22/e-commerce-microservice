package com.itsthatjun.ecommerce.dto.oms.admin;

import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AdminOrderDetail {

    @ApiModelProperty("order")
    private Orders order;

    @ApiModelProperty("order item list")
    private List<OrderItem> orderItemList;

    @ApiModelProperty("")
    private Address address;

}
