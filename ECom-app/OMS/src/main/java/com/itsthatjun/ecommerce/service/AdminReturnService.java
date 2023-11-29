package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminReturnService {

    @ApiOperation("return request based on status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(with reason) 3")
    Flux<ReturnRequest> getAllReturnRequest(int statusCode);

    @ApiOperation("Get users all return requests")
    Flux<ReturnDetail> getUserAllReturnDetail(int userId);

    @ApiOperation("Get detail of return request")
    Mono<ReturnDetail> getReturnDetail(String orderSn);

    @ApiOperation("")
    Mono<ReturnRequest> approveReturnRequest(ReturnRequestDecision returnRequestDecision, String operator);

    @ApiOperation("")
    Mono<ReturnRequest> rejectReturnRequest(ReturnRequestDecision returnRequestDecision, String reason, String operator);

    @ApiOperation("")
    Mono<ReturnRequest> completeReturnRequest(ReturnRequestDecision returnRequestDecision, String operator);
}
