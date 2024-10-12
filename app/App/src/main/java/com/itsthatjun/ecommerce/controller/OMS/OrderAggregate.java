package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.URLUtils;
import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/detail/{orderSn}")
    @ApiOperation("Get Order Detail by serial number")
    public Mono<OrderDetail> detail(@PathVariable String orderSn) {
        return orderService.detail(orderSn);
    }

    @GetMapping("/list")
    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    public Flux<OrderDTO> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return orderService.list(status, pageNum, pageSize);
    }

    @PostMapping("/generateOrder")
    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    public Mono<OrderParam> generateOrder(@RequestBody OrderParam orderParam, ServerRequest request) {
        String requestUrl = URLUtils.getBaseUrl(request);
        return orderService.generateOrder(orderParam, requestUrl);
    }

    @GetMapping("/payment/success")
    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    public Mono<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        return orderService.successPay(paymentId, payerId);
    }

    @GetMapping("/payment/getPaymentLink/{orderSn}")
    @ApiOperation("Pay order payment at a different time than generating order.")
    public Mono<String> getPaymentLink(@PathVariable String orderSn) {
        return orderService.getPaymentLink(orderSn);
    }

    @PostMapping("/cancelOrder/{orderSn}")
    @ApiOperation("Cancel order if before sending the order out.")
    public Mono<Void> cancelUserOrder(@PathVariable String orderSn) {
        return orderService.cancelUserOrder(orderSn);
    }
}
