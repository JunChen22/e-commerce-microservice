package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dao.ReturnDao;
import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.mbg.model.ReturnReasonPictures;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class ReturnController {

    private final ReturnOrderServiceImpl returnOrderService;

    private final ReturnDao dao;

    @Autowired
    public ReturnController(ReturnOrderServiceImpl returnOrderService, ReturnDao dao) {
        this.returnOrderService = returnOrderService;
        this.dao = dao;
    }

    @GetMapping("/status/{orderSn}")
    @ApiOperation(value = "check status of the return request")
    public Mono<ReturnRequest> checkStatus(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId){
        return returnOrderService.getStatus(orderSn, userId);
    }

    @PostMapping("/apply")
    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    public Mono<ReturnRequest> applyForReturn(@RequestBody ReturnParam returnParam, @RequestParam int userId){
        ReturnRequest returnRequest = returnParam.getReturnRequest();
        List<ReturnReasonPictures> picturesList = returnParam.getPicturesList();
        Map<String, Integer> skuQuantity = returnParam.getSkuQuantity();
        return returnOrderService.applyForReturn(returnRequest, picturesList, skuQuantity, userId);
    }

    @PostMapping("/update")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<ReturnRequest> updateReturn(@RequestBody ReturnParam returnParam, @RequestParam int userId){
        ReturnRequest returnRequest = returnParam.getReturnRequest();
        List<ReturnReasonPictures> picturesList = returnParam.getPicturesList();
        Map<String, Integer> skuQuantity = returnParam.getSkuQuantity();
        return returnOrderService.updateReturnInfo(returnRequest, picturesList, returnRequest.getOrderSn(), userId);
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<ReturnRequest> cancelReturn(@RequestParam String orderSn, @RequestParam int userId){
        return returnOrderService.cancelReturn(orderSn, userId);
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @GetMapping("/admin/AllOpening")
    @ApiOperation(value = "list all return request open waiting to be approved")
    public Flux<ReturnRequest> listAllOpening(){
        return returnOrderService.getAllOpening();
    }

    @GetMapping("/admin/AllReturning")
    @ApiOperation(value = "List all returns that are on their way")
    public Flux<ReturnRequest> listReturning(){
        return returnOrderService.getAllReturning();
    }

    @GetMapping("/admin/AllCompleted")
    @ApiOperation(value = "List ALl completed returns")
    public Flux<ReturnRequest> listAllCompleted(){
        return returnOrderService.getAllCompleted();
    }

    @GetMapping("/admin/AllRejected")
    @ApiOperation(value = "List All rejected returns requests")
    public Flux<ReturnRequest> listAllRejected(){
        return returnOrderService.getAllRejected();
    }

    @GetMapping("/admin/{serialNumber}")
    @ApiOperation(value = "return a return request detail")
    public Mono<ReturnDetail> getReturnRequest(@PathVariable String serialNumber){
        return returnOrderService.getReturnDetail(serialNumber);
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "update the status of the return apply")
    public ReturnRequestDecision updateReturnOrderStatus(@RequestBody ReturnRequestDecision returnRequestDecision, HttpSession session){
        ReturnRequest returnApply = returnRequestDecision.getReturnRequest();

        String operator = (String) session.getAttribute("adminName");
        returnRequestDecision.getReturnRequest().setHandleOperator(operator);

        switch (returnRequestDecision.getStatus()) {
            case APPROVED:
                returnOrderService.approveReturnRequest(returnRequestDecision, operator);
                break;
            case REJECTED:
                String rejectionReason = returnRequestDecision.getReason();
                returnOrderService.rejectReturnRequest(returnRequestDecision, rejectionReason, operator);
                break;
            case COMPLETED_RETURN:
                returnOrderService.completeReturnRequest(returnRequestDecision, operator);
                break;
            default:
                throw new IllegalArgumentException("Invalid status");
        }
        return returnRequestDecision;
    }
}
