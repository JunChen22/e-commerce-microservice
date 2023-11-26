package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.config.URLUtils;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.outgoing.OrderDTO;
import com.itsthatjun.ecommerce.security.UserContext;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "Order controller", description = "Order controller")
@RequestMapping("/order")
public class OrderAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAggregate.class);

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderAggregate(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/detail/{orderSn}")
    @ApiOperation("Get Order Detail by serial number")
    public Mono<OrderDetail> detail(@PathVariable String orderSn) {
        int userId = getUserId();
        return orderService.detail(orderSn, userId);
    }

    @GetMapping("/list")
    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    public Flux<OrderDTO> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        int userId = getUserId();
        return orderService.list(status, pageNum, pageSize, userId);
    }

    @PostMapping("/generateOrder")
    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    public Mono<OrderParam> generateOrder(@RequestBody OrderParam orderParam, HttpServletRequest request) {
        int userId = getUserId();
        String requestUrl = URLUtils.getBaseURl(request);
        return orderService.generateOrder(orderParam, requestUrl, userId);
    }

    @GetMapping("/payment/success")
    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    public Mono<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        return orderService.successPay(paymentId, payerId);
    }

    @GetMapping("/payment/getPaymentLink/{orderSn}")
    @ApiOperation("Pay order payment at a different time than generating order.")
    public Mono<String> getPaymentLink(@PathVariable String orderSn) {
        int userId = getUserId();
        return orderService.getPaymentLink(orderSn, userId);
    }

    @PostMapping("/cancelOrder/{orderSn}")
    @ApiOperation("Cancel order if before sending the order out.")
    public Mono<Void> cancelUserOrder(@PathVariable String orderSn) {
        int userId = getUserId();
        return orderService.cancelUserOrder(orderSn, userId);
    }

    private int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();
        return userId;
    }
}
