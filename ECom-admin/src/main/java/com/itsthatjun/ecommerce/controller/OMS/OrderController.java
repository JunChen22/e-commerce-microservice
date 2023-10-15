package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderStatus;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order")
@Api(tags = "Order related", description = "retrieve information about an order(s) and change order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all/{statusCode}")
    @ApiOperation(value = "List all orders that are delivered")
    public Flux<Orders> listAllOrder(@PathVariable OrderStatus statusCode) {
        return orderService.listAllOrder(statusCode);
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "get all orders made by user")
    public Flux<Orders> getUserOrders(@PathVariable int userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "look up a order by serial number")
    public Mono<OrderDetail> getOrder(@PathVariable String serialNumber) {
        return orderService.getOrder(serialNumber);
    }

    @PostMapping("/create")
    @ApiOperation(value = "create order")
    public Mono<OrderDetail> createOrder(@RequestBody OrderParam orderParam, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        OrderDetail orderDetail = orderParam.getOrderDetail();
        String reason = orderParam.getReason();
        return orderService.createOrder(orderDetail, reason, operatorName);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a order")
    public Mono<OrderDetail> updateOrder(@RequestBody OrderParam orderParam, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        OrderDetail orderDetail = orderParam.getOrderDetail();
        String reason = orderParam.getReason();
        return orderService.updateOrder(orderDetail, reason, operatorName);
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "cancel a order by serial number")
    public Mono<Void> cancelOrder(@RequestBody OrderParam orderParam, HttpSession session) {
        String orderSn = orderParam.getOrderDetail().getOrders().getOrderSn();
        String operatorName  = (String) session.getAttribute("adminName");
        String reason = orderParam.getReason();
        return orderService.cancelOrder(orderSn, reason, operatorName);
    }
}
