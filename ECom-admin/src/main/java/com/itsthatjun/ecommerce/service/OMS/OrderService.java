package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.OrderStatus;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
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
    Mono<OrderDetail> getOrder(String serialNumber);

    @ApiOperation(value = "create order")
    Mono<OrderDetail> createOrder(OrderDetail orderDetail, int userId, String reason, String operatorName);

    @ApiOperation(value = "update a order")
    Mono<OrderDetail> updateOrder(OrderDetail orderDetail, String reason, String operatorName);

    @ApiOperation(value = "delete a order by serial number")
    Mono<Void> cancelOrder(String serialNumber, int userId, String reason, String operatorName);
}
