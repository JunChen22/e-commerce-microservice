package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderStatus;
import com.itsthatjun.ecommerce.dto.oms.admin.AdminOrderDetail;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
@PreAuthorize("hasRole('ROLE_admin_order')")
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
    public Mono<AdminOrderDetail> getOrder(@PathVariable String serialNumber) {
        return orderService.getOrder(serialNumber);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPermission('order:create')")
    @ApiOperation(value = "create order")
    public Mono<AdminOrderDetail> createOrder(@RequestBody OrderParam orderParam) {
        AdminOrderDetail orderDetail = orderParam.getOrderDetail();
        String reason = orderParam.getReason();
        return orderService.createOrder(orderDetail, reason);
    }

    @GetMapping("/payment/getPaymentLink/{orderSn}")
    @ApiOperation("Pay order payment at a different time than generating order.")
    public Mono<String> getPaymentLink(@PathVariable String orderSn) {
        return orderService.getPaymentLink(orderSn);
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission('order:update')")
    @ApiOperation(value = "update a order")
    public Mono<AdminOrderDetail> updateOrder(@RequestBody OrderParam orderParam) {
        AdminOrderDetail orderDetail = orderParam.getOrderDetail();
        String reason = orderParam.getReason();
        return orderService.updateOrder(orderDetail, reason);
    }

    @DeleteMapping("/cancel")
    @PreAuthorize("hasPermission('order:delete')")
    @ApiOperation(value = "cancel a order by serial number")
    public Mono<Void> cancelOrder(@RequestBody OrderParam orderParam) {
        String orderSn = orderParam.getOrderDetail().getOrder().getOrderSn();
        String reason = orderParam.getReason();
        return orderService.cancelOrder(orderSn, reason);
    }

    @PostMapping("/notification/")
    @PreAuthorize("hasPermission('order:update')")
    @ApiOperation(value = "Send message to users that are affected by purchased product sku")
    public Mono<Void> sendOrderItemNotification(@RequestParam String productSku, @RequestBody String message) {
        return orderService.sendOrderItemNotification(productSku, message);
    }

    @PostMapping("/notification/{productName}")
    @PreAuthorize("hasPermission('order:update')")
    @ApiOperation(value = "Send message to users that are affected by purchased product")
    public Mono<Void> sendOrderProductNotification(@PathVariable String productName, @RequestBody String message) {
        return orderService.sendOrderProductNotification(productName, message);
    }
}
