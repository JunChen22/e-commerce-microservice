package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AdminOrderService {

    @ApiOperation("waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5")
    Flux<Orders> getAllOrderByStatus(int statusCode);

    @ApiOperation("get user detail order")
    Mono<OrderDetail> getUserOrderDetail(String orderSn);

    @ApiOperation("get all orders from user")
    Flux<Orders> getUserOrders(int memberId);

    @ApiOperation("get payment link to make payment in different time")
    Mono<String> getUserOrderPaymentLink(String orderSn);

    @ApiOperation("Admin created order for user, to fix mistake on order or order a replacement. free or pay later")
    Mono<Orders> createOrder(Orders newOrder, List<OrderItem> orderItemList, Address address, String reason, String operator);

    @ApiOperation("Update order status")
    Mono<Orders> updateOrder(Orders updateOrder, String reason, String operator);

    @ApiOperation("delete an order")
    Mono<Void> adminCancelOrder(Orders updateOrder, String reason, String operator);

    @ApiOperation("send out announcement/notification to use who purchased a item/sku")
    Mono<Void> adminOrderItemAnnouncement(String productSku, String message, String operator);

    @ApiOperation("send out announcement/notification to use who purchased a product of any variant/sku")
    Mono<Void> adminOrderProductAnnouncement(String productName, String message, String operator);
}
