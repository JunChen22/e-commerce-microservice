package com.itsthatjun.ecommerce.dto.oms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderParam {

    @ApiModelProperty("")
    private OrderDetail orderDetail;

    @ApiModelProperty("")
    private String reason;

    @ApiModelProperty("")
    private  int userId;
}
