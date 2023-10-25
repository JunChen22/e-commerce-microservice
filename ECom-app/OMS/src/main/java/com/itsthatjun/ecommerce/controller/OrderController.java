package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.impl.OrderServiceImpl;
import com.paypal.api.payments.Order;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
@Api(tags = "", description = "")
public class OrderController {

    private final static Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @GetMapping("/list")
    public Flux<Orders> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                             @RequestHeader("X-UserId") int userId) {
        return orderService.list(status,pageNum,pageSize, userId);
    }

    @ApiOperation("Get Order Detail by serial number")
    @GetMapping("/detail/{orderSn}")
    public Mono<OrderDetail> detail(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId) {
        return orderService.getOrdeDetail(orderSn, userId);
    }

    @ApiOperation("Get payment link to pay later")
    @GetMapping("/payment/getPaymentLink/{orderSn}")
    public Mono<String> getPaymentLink(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId) {
        return orderService.getPaymentLink(orderSn, userId);
    }

    @GetMapping("/admin/all")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<Orders> getAllOrderByStatus(@RequestParam int statusCode) {
        return orderService.getAllOrderByStatus(statusCode);
    }

    @GetMapping("/admin/detail/{orderSn}")
    @ApiOperation(value = "get user order detail")
    public Mono<OrderDetail> getUserOrderDetail(@PathVariable String orderSn) {
        return orderService.getUserOrderDetail(orderSn);
    }

    @GetMapping("/admin/user/{userId}")
    @ApiOperation(value = "get user order detail")
    public Flux<Orders> getUserOrderDetail(@PathVariable int userId) {
        return orderService.getUserOrders(userId);
    }

    @ApiOperation("Get payment link to pay later")
    @GetMapping("/admin/payment/getPaymentLink/{orderSn}")
    public Mono<String> getUserPaymentLink(@PathVariable String orderSn) {
        return orderService.getUserOrderPaymentLink(orderSn);
    }
}
