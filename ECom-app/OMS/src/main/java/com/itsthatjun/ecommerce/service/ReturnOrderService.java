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
    @ApiOperation("view return apply status, approve/reject")
    Mono<ReturnDetail> getStatus(String orderSn, int userId);

    @ApiOperation("apply for return, waiting for admin approve/reject")
    Mono<ReturnRequest> applyForReturn(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList, Map<String, Integer> skuQuantity, int userId);

    @ApiOperation("update or add in more info for return")
    Mono<ReturnRequest> updateReturnInfo(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList, String orderSn, int userId);

    @ApiOperation("cancel the return request")
    Mono<ReturnRequest> cancelReturn(String orderSn, int userId);

    @ApiOperation("automatically reject after a set time, not review by admin")
    Mono<ReturnRequest> delayedRejectReturn(String orderSn, int userId);

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
