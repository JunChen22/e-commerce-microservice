package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.config.URLUtils;
import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.impl.OrderServiceImpl;
import com.itsthatjun.ecommerce.service.PaypalService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    public static final String PAYPAL_SUCCESS_URL = "order/success";
    public static final String PAYPAL_CANCEL_URL = "order/cancel";

    private final static Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrderServiceImpl orderService;

    @Autowired
    private PaypalService paypalService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @GetMapping("/list")
    public Flux<Orders> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                             @RequestParam int userId) {
        return orderService.list(status,pageNum,pageSize, userId);
    }

    @ApiOperation("Get Order Detail by serial number")
    @GetMapping("/detail/{orderSn}")
    public Mono<OrderDetail> detail(@PathVariable String orderSn) {
        return orderService.getOrdeDetail(orderSn);
    }


    @PostMapping("/generateOrder")
    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    public Mono<Orders> generateOrder(@RequestBody OrderParam orderParam , HttpServletRequest request, HttpSession session, @RequestParam int userId){
        session.setAttribute("shippingAddress", orderParam.getAddress());
        session.setAttribute("couponCode", orderParam.getCoupon());

        String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
        String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;

        return orderService.generateOrder(orderParam, successUrl, cancelUrl, userId);
    }

    @GetMapping("/success")
    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam String orderSn){
        orderService.paySuccess(orderSn, paymentId, payerId);
        return "redirect:/";
    }

    @ApiOperation("Payment failure feedback")
    @GetMapping("/cancel")
    public String payFail(@RequestParam String orderSn) {
        orderService.payFail(orderSn);
        return null;
    }

    @ApiOperation("Cancel order")
    @PostMapping("/cancelOrder/{orderSn}")
    public Mono<Orders> cancelUserOrder(@PathVariable String orderSn) {
        return orderService.cancelOrder(orderSn);
    }

    // TODO: redis or like Quartz
    // timed/schedule depend on the deliver time and check UPS
    // then called to change status by redis.
    @ApiOperation("Member received deliver, update order status")
    @PostMapping(value = "/confirmReceiveOrder/{orderId}")
    public String confirmReceiveOrder(@PathVariable int orderId) {
        return null;
    }

    @GetMapping("/admin/payment")
    @ApiOperation(value = "List all orders that need to be paid")
    public Flux<Orders> listAllOrdersWaitForPayment(){
        return orderService.getAllWaitingForPayment();
    }

    @GetMapping("/admin/fulfill")
    @ApiOperation(value = "List all orders that need to be fulfill/open")
    public Flux<Orders> listAllFulfullingOrders(){
        return orderService.getAllFulfulling();
    }

    @GetMapping("/admin/send")
    @ApiOperation(value = "List all orders that are send")
    public Flux<Orders> listAllSendOrders(){
        return orderService.getAllInSend();
    }

    @GetMapping("/admin/complete")
    @ApiOperation(value = "List all orders that are delivered")
    public Flux<Orders> listAl(){
        return orderService.getAllCompleteOrder();
    }

    @GetMapping("/admin/user/{id}")
    @ApiOperation(value = "get all orders made by user")
    public Flux<Orders> getUserOrders(@PathVariable int id){
        return orderService.getUserOrders(id);
    }

    @GetMapping("/admin/{serialNumber}")
    @ApiOperation(value = "look up a order by serial number")
    public Mono<OrderDetail> getOrder(@PathVariable String serialNumber){
        return orderService.getOrdeDetail(serialNumber);
    }

    @PostMapping("/admin/create")
    @ApiOperation(value = "create order")
    public Mono<Orders> createOrder(@RequestBody OrderDetail newOrderDetail){

        Orders newOrder = newOrderDetail.getOrders();
        List<OrderItem> orderItemList = newOrderDetail.getOrderItemList();

        return orderService.createOrder(newOrder, orderItemList, "", "");
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "update a order")
    public Mono<Orders> updateOrder(@RequestBody Orders updateOrder){
        return orderService.updateOrder(updateOrder, "", "");
    }

    @DeleteMapping("/admin/delete/{serialNumber}")
    @ApiOperation(value = "delete a order by serial number")
    public void deleteOrder(@RequestBody Orders orderToDelete){
        orderService.adminCancelOrder(orderToDelete, "", "");
    }
}
