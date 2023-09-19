package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OrderDetail{

    @ApiModelProperty("order item list")
    private Orders orders;

    @ApiModelProperty("order item list")
    private List<OrderItem> orderItemList;

    @ApiModelProperty("Member deliver address")
    private Address address;
}
