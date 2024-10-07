package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order/return")
@Tag(name = "return related", description = "apply return and related api")
public class ReturnController {

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
