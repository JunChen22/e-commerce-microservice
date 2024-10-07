package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderReturnServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order/return")
@PreAuthorize("hasRole('ROLE_admin_order')")
@Tag(name = "return related", description = "apply return and related api")
public class OrderReturnController {

    private final OrderReturnServiceImpl orderReturnService;

    @Autowired
    public OrderReturnController(OrderReturnServiceImpl orderReturnService) {
        this.orderReturnService = orderReturnService;
    }

    @GetMapping("/all/{statusCode}")
    @ApiOperation(value = "list all return request open waiting to be approved")
    public Flux<ReturnRequest> listAllReturnRequest(@PathVariable ReturnStatusCode statusCode) {
        return orderReturnService.listAllReturnRequest(statusCode);
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "return a return request detail")
    public Mono<ReturnDetail> getReturnRequest(@PathVariable String serialNumber) {
        return orderReturnService.getReturnRequest(serialNumber);
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "return a return request detail")
    public Flux<ReturnDetail> getUserAllRequest(@PathVariable int userId) {
        return orderReturnService.listUserAllReturnRequest(userId);
    }

    // TODO: add return request created by admin, currently is admin updating
    // the status of the return request created by user

    @PostMapping("/update")
    @PreAuthorize("hasPermission('order:update')")
    @ApiOperation(value = "update the status of the return apply")
    public Mono<Void> updateReturnOrderStatus(@RequestBody ReturnRequestDecision returnRequestDecision) {
        return orderReturnService.updateReturnOrderStatus(returnRequestDecision);
    }
}
