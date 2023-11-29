package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import com.itsthatjun.ecommerce.dto.oms.model.OrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetail extends OrderDTO {

    @ApiModelProperty("order item list")
    private List<OrderItemDTO> orderItemList;
}
