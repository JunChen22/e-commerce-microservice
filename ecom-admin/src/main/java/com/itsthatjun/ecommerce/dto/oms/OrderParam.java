package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.admin.AdminOrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderParam {

    @ApiModelProperty("")
    private AdminOrderDetail orderDetail;

    @ApiModelProperty("")
    private String reason;

    @ApiModelProperty("")
    private  int userId;
}
