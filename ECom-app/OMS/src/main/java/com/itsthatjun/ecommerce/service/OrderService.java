package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    @ApiOperation(value = "")
    Mono<OrderDetail> getOrderDetail(String orderSn, int userId);

    @ApiOperation("list all user orders")
    Flux<Orders> list(int status, int pageNum, int pageSize, int userId);

    @ApiOperation(value = "get payment link to make payment in different time")
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

    @ApiOperation(value = "get all order that are still in status 2, send/transitioning")
    List<Orders> getAllSendingOrder();

    @ApiOperation("Member received deliver, update order status by scheduled task")
    void confirmReceiveOrder(String orderSn);

    @ApiOperation(value = "waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5")
    Flux<Orders> getAllOrderByStatus(int statusCode);

    @ApiOperation(value = "get user detail order")
    Mono<OrderDetail> getUserOrderDetail(String orderSn);

    @ApiOperation(value = "get all orders from user")
    Flux<Orders> getUserOrders(int memberId);

    @ApiOperation(value = "get payment link to make payment in different time")
    Mono<String> getUserOrderPaymentLink(String orderSn);

    @ApiOperation(value = "Admin created order for user, to fix mistake on order or order a replacement. free or pay later")
    Mono<Orders> createOrder(Orders newOrder, List<OrderItem> orderItemList, Address address, String reason, String operator);

    @ApiOperation(value = "Update order status")
    Mono<Orders> updateOrder(Orders updateOrder, String reason, String operator);

    @ApiOperation(value = "delete an order")
    Mono<Void> adminCancelOrder(Orders updateOrder, String reason, String operator);
}
