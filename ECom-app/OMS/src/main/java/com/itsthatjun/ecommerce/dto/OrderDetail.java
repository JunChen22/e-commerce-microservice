package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.OrderDTO;
import com.itsthatjun.ecommerce.dto.model.OrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDetail extends OrderDTO {

    @ApiModelProperty("order item list")
    private List<OrderItemDTO> orderItemList;
}
