package com.itsthatjun.ecommerce.controller.admin;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.admin.AdminReturnServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order/return/admin")
public class AdminReturnController {

    private final AdminReturnServiceImpl returnOrderService;

    @Autowired
    public AdminReturnController(AdminReturnServiceImpl returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<ReturnRequest> getAllRequestByStatus(@RequestParam int statusCode) {
        return returnOrderService.getAllReturnRequest(statusCode);
    }

    @GetMapping("/user/all/{userId}")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<ReturnDetail> getUserAllRequest(@PathVariable int userId) {
        return returnOrderService.getUserAllReturnDetail(userId);
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "get a return detail by order serial number")
    public Mono<ReturnDetail> getReturnDetailBySn(@PathVariable String serialNumber) {
        return returnOrderService.getReturnDetail(serialNumber);
    }
}
