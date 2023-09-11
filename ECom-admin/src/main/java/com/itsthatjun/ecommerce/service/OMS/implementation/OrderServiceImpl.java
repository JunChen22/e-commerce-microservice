package com.itsthatjun.ecommerce.service.OMS.implementation;

import com.itsthatjun.ecommerce.exceptions.OMS.OrderException;
import com.itsthatjun.ecommerce.mbg.mapper.OrdersMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.OMS.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrdersMapper ordersMapper;

    @Autowired
    public OrderServiceImpl(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    // TODO: change these based of status code and add 4 and 5 in
    // waiting for payment 0 , fulfilling 1,  send 2 , complete(received) 3, closed(out of return period) 4 ,invalid 5
    @Override
    public List<Orders> getAllWaitingForPayment() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(0);
        List<Orders> orderWaitingForPayment = ordersMapper.selectByExample(example);
        return orderWaitingForPayment;
    }

    @Override
    public List<Orders> getAllFulfulling() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(1);
        List<Orders> orderWaitingToBeFulfill = ordersMapper.selectByExample(example);
        return orderWaitingToBeFulfill;
    }

    @Override
    public List<Orders> getAllInSend() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(2);
        List<Orders> orderInTransit = ordersMapper.selectByExample(example);
        return orderInTransit;
    }

    @Override
    public List<Orders> getAllCompleteOrder() {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andStatusEqualTo(3);
        List<Orders> orderCompletedOrder = ordersMapper.selectByExample(example);
        return orderCompletedOrder;
    }

    @Override
    public Orders getOrderByOrderNumber(String orderSerialNumber) {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andOrderSnEqualTo(orderSerialNumber);
        List<Orders> ordersList = ordersMapper.selectByExample(example);
        if (ordersList.size() == 0)
            throw new OrderException("Order number does not exist: " + orderSerialNumber);
        return ordersList.get(0);
    }

    @Override
    public List<Orders> getUserOrders(int memberId) {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<Orders> ordersList = ordersMapper.selectByExample(example);
        return ordersList;
    }

    @Override
    public Orders createOrder(Orders newOrder, String adminName) {

        //  TODO: need to generate order serial number
        String orderSn = generateOrderSn();
        newOrder.setOrderSn(orderSn);
        newOrder.setCreatedAt(new Date());

        OrderChangeHistory changeHistory = new OrderChangeHistory();
        changeHistory.setChangeOperator(adminName);
        changeHistory.setCreatedAt(new Date());
        changeHistory.setOrderStatus(1);

        int status = ordersMapper.insert(newOrder);
        return newOrder;
    }

    @Override
    public Orders updateOrder(Orders updateOrder) {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andOrderSnEqualTo(updateOrder.getOrderSn());

        int status = ordersMapper.updateByExample(updateOrder, example);

        return updateOrder;
    }

    @Override
    public void deleteOrder(String orderSerialNumber) {
        OrdersExample example = new OrdersExample();
        example.createCriteria().andOrderSnEqualTo(orderSerialNumber);
        ordersMapper.deleteByExample(example);
    }

    // TODO: use a better way to generate order serial number
    private String generateOrderSn() {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
        //Long increment = redisService.incr(key, 1);
        sb.append(date);
        sb.append("A BETTER WAY TO GENERATE SN");
        //sb.append(String.format("%02d", order.getSourceType()));
        //sb.append(String.format("%02d", order.getPayType()));
        //String incrementStr = increment.toString();
        //if (incrementStr.length() <= 6) {
        //    sb.append(String.format("%06d", increment));
        //} else {
        //    sb.append(incrementStr);
        //}
        return sb.toString();
    }
}
