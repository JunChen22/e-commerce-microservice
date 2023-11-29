package com.itsthatjun.ecommerce.controller.admin;

import com.itsthatjun.ecommerce.controller.OrderController;
import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.admin.AdminOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order/admin")
@Api(tags = "", description = "")
public class AdminOrderController {

    private final static Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final AdminOrderServiceImpl orderService;

    @Autowired
    public AdminOrderController(AdminOrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "get all of the return request based on status code")
    public Flux<Orders> getAllOrderByStatus(@RequestParam int statusCode) {
        return orderService.getAllOrderByStatus(statusCode);
    }

    @GetMapping("/detail/{orderSn}")
    @ApiOperation(value = "get user order detail")
    public Mono<OrderDetail> getUserOrderDetail(@PathVariable String orderSn) {
        return orderService.getUserOrderDetail(orderSn);
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "get user order detail")
    public Flux<Orders> getUserOrderDetail(@PathVariable int userId) {
        return orderService.getUserOrders(userId);
    }

    @ApiOperation("Get payment link to pay later")
    @GetMapping("/payment/getPaymentLink/{orderSn}")
    public Mono<String> getUserPaymentLink(@PathVariable String orderSn) {
        return orderService.getUserOrderPaymentLink(orderSn);
    }
}
