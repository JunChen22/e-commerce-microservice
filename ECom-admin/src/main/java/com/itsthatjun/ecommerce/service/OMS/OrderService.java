package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface OrderService {

    //  waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5
    @ApiOperation(value = "")
    List<Orders> getAllWaitingForPayment();

    @ApiOperation(value = "")
    List<Orders> getAllFulfulling();

    @ApiOperation(value = "")
    List<Orders> getAllInSend();

    @ApiOperation(value = "")
    List<Orders> getAllCompleteOrder();

    // get by order number
    @ApiOperation(value = "")
    Orders getOrderByOrderNumber(String orderSerialNumber);

    // get all orders from user
    @ApiOperation(value = "")
    List<Orders> getUserOrders(int memberId);

    // create order
    @ApiOperation(value = "")
    Orders createOrder(Orders newOrder);

    // update an order
    @ApiOperation(value = "")
    Orders updateOrder(Orders updateOrder);

    // delete an order
    @ApiOperation(value = "")
    void deleteOrder(String orderSerialNumber);
}
