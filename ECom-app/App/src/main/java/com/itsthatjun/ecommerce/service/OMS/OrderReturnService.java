package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface OrderReturnService {

    @ApiOperation(value = "check status of the return request")
    Mono<ReturnRequest> checkStatus(String orderSn, int userId);

    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    Mono<ReturnParam> applyForReturn(ReturnParam returnParam, int userId);

    @ApiOperation(value = "change detail about return or return reason")
    Mono<ReturnParam> updateReturn(ReturnParam returnParam, int userId);

    @ApiOperation(value = "change detail about return or return reason")
    Mono<Void> cancelReturn(String orderSn, int userId);
}
