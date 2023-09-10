package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ReturnOrderService {

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3

    @ApiOperation(value = "")
    List<OrderReturnApply> getAllOpening();

    @ApiOperation(value = "")
    List<OrderReturnApply> getAllReturning();

    @ApiOperation(value = "")
    List<OrderReturnApply> getAllCompleted();

    @ApiOperation(value = "")
    List<OrderReturnApply> getAllRejected();

    // get specific return
    @ApiOperation(value = "")
    OrderReturnApply getOrderReturnDetail(String orderSn);

    // approve return request
    @ApiOperation(value = "")
    OrderReturnApply approveReturnRequest(OrderReturnApply returnRequest);

    // reject return request
    @ApiOperation(value = "")
    OrderReturnApply rejectReturnRequest(OrderReturnApply returnRequest, String reason);

    @ApiOperation(value = "")
    OrderReturnApply completeReturnRequest(OrderReturnApply returnRequest);
}
