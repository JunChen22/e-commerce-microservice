package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

}
