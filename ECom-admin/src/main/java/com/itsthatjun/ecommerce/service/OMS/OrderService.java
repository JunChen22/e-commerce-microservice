package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderStatus;
import com.itsthatjun.ecommerce.dto.oms.admin.AdminOrderDetail;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    @ApiOperation(value = "List all orders that are delivered")
    Flux<Orders> listAllOrder(OrderStatus statusCode);

    @ApiOperation(value = "get all orders made by user")
    Flux<Orders> getUserOrders(int userId);

    @ApiOperation(value = "look up a order by serial number")
    Mono<AdminOrderDetail> getOrder(String serialNumber);

    @ApiOperation(value = "create order")
    Mono<AdminOrderDetail> createOrder(AdminOrderDetail orderDetail, String reason, String operatorName);

    @ApiOperation(value = "get payment link to make payment in different time")
    Mono<String> getPaymentLink(String orderSn);

    @ApiOperation(value = "update a order")
    Mono<AdminOrderDetail> updateOrder(AdminOrderDetail orderDetail, String reason, String operatorName);

    @ApiOperation(value = "delete a order by serial number")
    Mono<Void> cancelOrder(String serialNumber, String reason, String operatorName);

    @ApiOperation(value = "Send message to users that are affected by purchased product sku")
    Mono<Void> sendOrderItemNotification(String productSku, String message, String operatorName);

    @ApiOperation(value = "Send message to users that are affected by purchased product")
    Mono<Void> sendOrderProductNotification(String productSku, String message, String operatorName);
}
