package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class ReturnController {

    private final static Logger LOG = LoggerFactory.getLogger(ReturnController.class);

    private final ReturnOrderServiceImpl returnOrderService;

    @Autowired
    public ReturnController(ReturnOrderServiceImpl returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    @GetMapping("/status/{orderSn}")
    @ApiOperation(value = "check status of the return request")
    public Mono<ReturnDetail> checkStatus(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId) {
        return returnOrderService.getStatus(orderSn, userId);
    }

    @GetMapping("/admin/all")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<ReturnRequest> getAllRequestByStatus(@RequestParam int statusCode) {
        return returnOrderService.getAllReturnRequest(statusCode);
    }

    @GetMapping("/admin/user/all/{userId}")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<ReturnDetail> getUserAllRequest(@PathVariable int userId) {
        return returnOrderService.getUserAllReturnDetail(userId);
    }

    @GetMapping("/admin/{serialNumber}")
    @ApiOperation(value = "get a return detail by order serial number")
    public Mono<ReturnDetail> getReturnDetailBySn(@PathVariable String serialNumber) {
        return returnOrderService.getReturnDetail(serialNumber);
    }
}
