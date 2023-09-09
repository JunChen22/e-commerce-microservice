package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReason;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public interface ReturnOrderService {

    @ApiModelProperty(value = "view return apply status, approve/reject")
    OrderReturnApply getStatus(String orderSn, int userId);

    @ApiModelProperty(value = "apply for return, waiting for admin approve/reject")
    OrderReturnApply applyForReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures, String orderSn, int userId);

    @ApiModelProperty(value = "update or add in more info for return")
    OrderReturnApply updateReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures, String orderSn, int userId);

    @ApiModelProperty(value = "cancel the return request")
    OrderReturnApply cancelReturn(String orderSn, int userId);


    // update service not for user to access, unless set to automatically approve return. this could be in admin instead of app
    // update the status of the order

    // return item received , update stocks
}
