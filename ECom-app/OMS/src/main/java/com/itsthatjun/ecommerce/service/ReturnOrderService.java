package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ReturnOrderService {

    @ApiOperation(value = "view return apply status, approve/reject")
    OrderReturnApply getStatus(String orderSn, int userId);

    @ApiOperation(value = "apply for return, waiting for admin approve/reject")
    OrderReturnApply applyForReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures, String orderSn, int userId);

    @ApiOperation(value = "update or add in more info for return")
    OrderReturnApply updateReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures, String orderSn, int userId);

    @ApiOperation(value = "cancel the return request")
    OrderReturnApply cancelReturn(String orderSn, int userId);



}
