package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.mbg.model.ReturnReasonPictures;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.ApiOperation;
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
}
