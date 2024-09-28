package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface OrderReturnService {

    @ApiOperation(value = "check status of the return request")
    Mono<ReturnDetail> checkStatus(String orderSn);

    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    Mono<ReturnParam> applyForReturn(ReturnParam returnParam);

    @ApiOperation(value = "change detail about return or return reason")
    Mono<ReturnParam> updateReturn(ReturnParam returnParam);

    @ApiOperation(value = "change detail about return or return reason")
    Mono<Void> cancelReturn(String orderSn);
}
