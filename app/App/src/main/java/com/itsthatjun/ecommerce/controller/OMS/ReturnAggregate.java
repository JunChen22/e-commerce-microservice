package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderReturnServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "Return controller", description = "return order")
@RequestMapping("/order/return")
public class ReturnAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnAggregate.class);

    private final OrderReturnServiceImpl returnService;

    @Autowired
    public ReturnAggregate(OrderReturnServiceImpl returnService) {
        this.returnService = returnService;
    }

    @GetMapping("/status/{orderSn}")
    @ApiOperation(value = "check status of the return request")
    public Mono<ReturnDetail> checkStatus(@PathVariable String orderSn) {
        return returnService.checkStatus(orderSn);
    }

    @PostMapping("/apply")
    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    public Mono<ReturnParam> applyForReturn(@RequestBody ReturnParam returnParam) {
        return returnService.applyForReturn(returnParam);
    }

    @PostMapping("/update")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<ReturnParam> updateReturn(@RequestBody ReturnParam returnParam) {
        return returnService.updateReturn(returnParam);
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<Void> cancelReturn(@RequestParam String orderSn) {
        return returnService.cancelReturn(orderSn);
    }
}
