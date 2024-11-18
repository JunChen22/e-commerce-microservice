package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.util.URLUtils;
import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Order controller", description = "Order controller")
@RequestMapping("/order")
public class OrderAggregate {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderAggregate(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get Order Detail by serial number", description = "Get Order Detail by serial number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Order Detail by serial number"),
            @ApiResponse(responseCode = "404", description = "Order not found")})
    @GetMapping("/detail/{orderSn}")
    public Mono<OrderDetail> detail(@PathVariable String orderSn) {
        return orderService.detail(orderSn);
    }

    @Operation(summary = "Get member order based on status code and page size", description = "Get member order based on status code and page size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get member order based on status code and page size"),
            @ApiResponse(responseCode = "404", description = "No orders found")})
    @GetMapping("/list")
    public Flux<OrderDTO> list(@RequestParam(required = false, defaultValue = "-1") int status,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return orderService.list(status, pageNum, pageSize);
    }

    @Operation(summary = "Generate order based on shopping cart, actual transaction", description = "Generate order based on shopping cart, actual transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generate order based on shopping cart, actual transaction"),
            @ApiResponse(responseCode = "404", description = "Order not generated")})
    @PostMapping("/generateOrder")
    public Mono<OrderParam> generateOrder(@RequestBody OrderParam orderParam, ServerRequest request) {
        String requestUrl = URLUtils.getBaseUrl(request);
        return orderService.generateOrder(orderParam, requestUrl);
    }

    @Operation(summary = "Pay order payment", description = "after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pay order payment"),
            @ApiResponse(responseCode = "404", description = "Order not paid")})
    @GetMapping("/payment/success")
    public Mono<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        return orderService.successPay(paymentId, payerId);
    }

    @Operation(summary = "Pay order payment", description = "Pay order payment at a different time than generating order.")
    @GetMapping("/payment/getPaymentLink/{orderSn}")
    public Mono<String> getPaymentLink(@PathVariable String orderSn) {
        return orderService.getPaymentLink(orderSn);
    }

    @Operation(summary = "Cancel order if before sending the order out.", description = "Cancel order if before sending the order out.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancel order if before sending the order out."),
            @ApiResponse(responseCode = "404", description = "Order not canceled")})
    @PostMapping("/cancelOrder/{orderSn}")
    public Mono<Void> cancelUserOrder(@PathVariable String orderSn) {
        return orderService.cancelUserOrder(orderSn);
    }
}
