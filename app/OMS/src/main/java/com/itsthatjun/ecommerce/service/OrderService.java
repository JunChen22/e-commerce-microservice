package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.dto.model.OrderDTO;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    @ApiOperation("")
    Mono<OrderDetail> getOrderDetail(String orderSn, int userId);

    @ApiOperation("list all user orders")
    Flux<OrderDTO> list(int status, int pageNum, int pageSize, int userId);

    @ApiOperation("get payment link to make payment in different time")
    Mono<String> getPaymentLink(String orderSn, int userId);

    @Transactional
    @ApiOperation("Create the actual transaction and payment")
    Mono<Orders> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId);

    @Transactional
    @ApiOperation("payment successful, redirected from paypal")
    Mono<Orders> paySuccess(String paymentId, String payerId);

    @ApiOperation("payment unsuccessful, redirected from paypal, store token to pay later, 30 minute ttl")
    Mono<Void> delayedCancelOrder(String orderSn);

    @ApiOperation("Member cancel order")
    Mono<Orders> cancelOrder(String orderSn);

    @ApiOperation("get all orders that are affected by product")
    List<OrderDetail> getUserPurchasedItem(String productSku);

    @ApiOperation("get all order that are still in status 2, send/transitioning")
    List<Orders> getAllSendingOrder();

    @ApiOperation("Member received deliver, update order status by scheduled task")
    void confirmReceiveOrder(String orderSn);
}
