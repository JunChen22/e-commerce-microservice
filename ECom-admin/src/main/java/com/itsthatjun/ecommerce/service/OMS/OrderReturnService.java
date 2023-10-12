package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderReturnService {

    @ApiOperation(value = "list all return request open waiting to be approved")
    Flux<ReturnRequest> listAllReturnRequest(ReturnStatusCode statusCode);

    @ApiOperation(value = "return a return request detail")
    Mono<ReturnRequest> getReturnRequest(String serialNumber);

    @ApiOperation(value = "update the status of the return apply")
    Mono<Void> updateReturnOrderStatus(ReturnRequestDecision returnRequestDecision, String operator);
}
