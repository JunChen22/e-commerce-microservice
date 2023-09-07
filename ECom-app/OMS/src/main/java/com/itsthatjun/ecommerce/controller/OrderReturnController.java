package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class OrderReturnController {

    private final ReturnOrderServiceImpl returnOrderService;

    @Autowired
    public OrderReturnController(ReturnOrderServiceImpl returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    @GetMapping("/status")
    @ApiOperation(value = "check status of the return request")
    public Map<String, Object> generateOrder(){
        return null;
    }

    @PostMapping("/apply")
    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    public Map<String, Object> applyForReturn(){
       return null;
    }

    @PostMapping("/update")
    @ApiOperation(value = "change detail about return or return reason")
    public Map<String, Object> updateReturn(){
        return null;
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "change detail about return or return reason")
    public void cancelReturn(){
    }
}
