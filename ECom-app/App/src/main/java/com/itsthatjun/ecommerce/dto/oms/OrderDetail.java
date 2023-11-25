package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.outgoing.OrderDTO;
import com.itsthatjun.ecommerce.dto.oms.outgoing.OrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetail extends OrderDTO {

    @ApiModelProperty("order item list")
    private List<OrderItemDTO> orderItemList;
}
