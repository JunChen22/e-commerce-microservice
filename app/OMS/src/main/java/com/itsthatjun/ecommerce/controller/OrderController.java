package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.model.OrderDTO;
import com.itsthatjun.ecommerce.service.impl.OrderServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/order")
@Tag(name = "", description = "")
public class OrderController {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @GetMapping("/list")
    public Flux<OrderDTO> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                               @RequestHeader("X-UserId") int userId) {
        return orderService.list(status, pageNum, pageSize, userId);
    }

    @GetMapping("/detail/{orderSn}")
    @ApiOperation("Get Order Detail by serial number")
    public Mono<OrderDetail> detail(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId) {
        return orderService.getOrderDetail(orderSn, userId);
    }

    @GetMapping("/payment/getPaymentLink/{orderSn}")
    @ApiOperation("Get payment link to pay later")
    public Mono<String> getPaymentLink(@PathVariable String orderSn, @RequestHeader("X-UserId") int userId) {
        return orderService.getPaymentLink(orderSn, userId);
    }

    @ApiOperation("get all orders that are affected by product")
    @GetMapping("/getUserPurchasedItem")
    public List<OrderDetail> getUserPurchasedItem(@RequestParam String productSku) {
        return orderService.getUserPurchasedItem(productSku);
    }
}
