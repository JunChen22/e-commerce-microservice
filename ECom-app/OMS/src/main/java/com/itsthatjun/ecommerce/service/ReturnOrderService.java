package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.mbg.model.ReturnReasonPictures;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ReturnOrderService {
    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @ApiOperation(value = "view return apply status, approve/reject")
    Mono<ReturnRequest> getStatus(String orderSn, int userId);

    @ApiOperation(value = "apply for return, waiting for admin approve/reject")
    Mono<ReturnRequest> applyForReturn(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList, Map<String, Integer> skuQuantity, int userId );

    @ApiOperation(value = "update or add in more info for return")
    Mono<ReturnRequest> updateReturnInfo(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList, String orderSn, int userId);

    @ApiOperation(value = "cancel the return request")
    Mono<ReturnRequest> cancelReturn(String orderSn, int userId);

    @ApiOperation(value = "")
    Flux<ReturnRequest> getAllOpening();

    @ApiOperation(value = "")
    Flux<ReturnRequest> getAllReturning();

    @ApiOperation(value = "")
    Flux<ReturnRequest> getAllCompleted();

    @ApiOperation(value = "")
    Flux<ReturnRequest> getAllRejected();

    @ApiOperation(value = "")
    Mono<ReturnDetail> getReturnDetail(String orderSn);

    @ApiOperation(value = "")
    Mono<ReturnRequest> approveReturnRequest(ReturnRequestDecision returnRequestDecision, String operator);

    @ApiOperation(value = "")
    Mono<ReturnRequest> rejectReturnRequest(ReturnRequestDecision returnRequestDecision, String reason, String operator);

    @ApiOperation(value = "")
    Mono<ReturnRequest> completeReturnRequest(ReturnRequestDecision returnRequestDecision, String operator);
}
