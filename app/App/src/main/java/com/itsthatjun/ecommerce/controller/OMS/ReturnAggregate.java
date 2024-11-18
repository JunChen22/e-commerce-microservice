package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnParam;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderReturnServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Return controller", description = "return order")
@RequestMapping("/order/return")
public class ReturnAggregate {

    private final OrderReturnServiceImpl returnService;

    @Autowired
    public ReturnAggregate(OrderReturnServiceImpl returnService) {
        this.returnService = returnService;
    }

    @Operation(summary = "check status of the return request", description = "check status of the return request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "check status of the return request"),
            @ApiResponse(responseCode = "404", description = "No return request found")})
    @GetMapping("/status/{orderSn}")
    public Mono<ReturnDetail> checkStatus(@PathVariable String orderSn) {
        return returnService.checkStatus(orderSn);
    }

    @Operation(summary = "Apply for return item, waiting for admin approve", description = "Apply for return item, waiting for admin approve")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apply for return item, waiting for admin approve"),
            @ApiResponse(responseCode = "404", description = "No return request found")})
    @PostMapping("/apply")
    public Mono<ReturnParam> applyForReturn(@RequestBody ReturnParam returnParam) {
        return returnService.applyForReturn(returnParam);
    }

    @Operation(summary = "change detail about return or return reason", description = "change detail about return or return reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change detail about return or return reason"),
            @ApiResponse(responseCode = "404", description = "No return request found")})
    @PostMapping("/update")
    public Mono<ReturnParam> updateReturn(@RequestBody ReturnParam returnParam) {
        return returnService.updateReturn(returnParam);
    }

    @Operation(summary = "change detail about return or return reason", description = "change detail about return or return reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change detail about return or return reason"),
            @ApiResponse(responseCode = "404", description = "No return request found")})
    @DeleteMapping("/cancel")
    public Mono<Void> cancelReturn(@RequestParam String orderSn) {
        return returnService.cancelReturn(orderSn);
    }
}
