package com.itsthatjun.ecommerce.service.OMS.implementation;

import com.itsthatjun.ecommerce.dto.OMS.event.PmsProductEvent;
import com.itsthatjun.ecommerce.dto.OMS.event.SmsSalesStockEvent;
import com.itsthatjun.ecommerce.exceptions.OMS.OrderReturnApplyException;
import com.itsthatjun.ecommerce.mbg.mapper.OrderReturnApplyMapper;
import com.itsthatjun.ecommerce.mbg.mapper.OrderReturnItemMapper;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApplyExample;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnItem;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnItemExample;
import com.itsthatjun.ecommerce.service.OMS.ReturnOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

    private final StreamBridge streamBridge;

    private final OrderReturnApplyMapper returnApplyMapper;

    private final OrderReturnItemMapper returnItemMapper;

    @Autowired
    public ReturnOrderServiceImpl(StreamBridge streamBridge, OrderReturnApplyMapper returnApplyMapper, OrderReturnItemMapper returnItemMapper) {
        this.streamBridge = streamBridge;
        this.returnApplyMapper = returnApplyMapper;
        this.returnItemMapper = returnItemMapper;
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @Override
    public List<OrderReturnApply> getAllOpening() {
        OrderReturnApplyExample example = new OrderReturnApplyExample();
        example.createCriteria().andStatusEqualTo(0);

        List<OrderReturnApply> orderReturnRequests = returnApplyMapper.selectByExample(example);

        return orderReturnRequests;
    }

    @Override
    public List<OrderReturnApply> getAllReturning() {
        OrderReturnApplyExample example = new OrderReturnApplyExample();
        example.createCriteria().andStatusEqualTo(1);
        List<OrderReturnApply> returningRequests = returnApplyMapper.selectByExample(example);

        return returningRequests;
    }

    @Override
    public List<OrderReturnApply> getAllCompleted() {
        OrderReturnApplyExample example = new OrderReturnApplyExample();
        example.createCriteria().andStatusEqualTo(2);
        List<OrderReturnApply> completedReturns = returnApplyMapper.selectByExample(example);

        return completedReturns;
    }

    @Override
    public List<OrderReturnApply> getAllRejected() {
        OrderReturnApplyExample example = new OrderReturnApplyExample();
        example.createCriteria().andStatusEqualTo(3);
        List<OrderReturnApply> rejectedRequests = returnApplyMapper.selectByExample(example);

        return rejectedRequests;
    }

    @Override
    public OrderReturnApply getOrderReturnDetail(String orderSn) {
        OrderReturnApplyExample example = new OrderReturnApplyExample();
        example.createCriteria().andOrderSnEqualTo(orderSn);

        List<OrderReturnApply> returnRequest = returnApplyMapper.selectByExample(example);
        if (returnRequest.isEmpty())
            throw new OrderReturnApplyException("Order Return request for order serial number: " + orderSn + " does not exist");
        return returnRequest.get(0);
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @Override
    public OrderReturnApply approveReturnRequest(OrderReturnApply returnRequest) {
        returnRequest.setStatus(1);
        returnApplyMapper.updateByPrimaryKey(returnRequest);
        return returnRequest;
    }

    @Override
    public OrderReturnApply rejectReturnRequest(OrderReturnApply returnRequest, String reason) {
        returnRequest.setStatus(3);
        returnRequest.setHandleNote(reason);
        returnApplyMapper.updateByPrimaryKey(returnRequest);
        return returnRequest;
    }

    @Override
    public OrderReturnApply completeReturnRequest(OrderReturnApply returnRequest) {
        returnRequest.setStatus(2);
        returnApplyMapper.updateByPrimaryKey(returnRequest);

        int userId = returnRequest.getMemberId();
        int orderId = returnRequest.getOrderId();
        String orderSn = returnRequest.getOrderSn();

        int returnApplyId = returnRequest.getId();

        OrderReturnItemExample returnItemExample = new OrderReturnItemExample();
        returnItemExample.createCriteria().andReturnApplyIdEqualTo(returnApplyId).andOrderSnEqualTo(orderSn);
        List<OrderReturnItem> returnedItemList = returnItemMapper.selectByExample(returnItemExample);

        Map<String, Integer> skuQuantity = new HashMap<>();

        for (OrderReturnItem returnItem : returnedItemList) {
            String skuCode = returnItem.getProductSku();
            int quantity = returnItem.getQuantity();
            skuQuantity.put(skuCode, quantity);
        }

        sendProductStockUpdateMessage("product-out-0", new PmsProductEvent(PmsProductEvent.Type.UPDATE_PURCHASE, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockEvent(SmsSalesStockEvent.Type.UPDATE_PURCHASE, orderSn, orderId, userId, skuQuantity));
        return returnRequest;
    }

    private void sendProductStockUpdateMessage(String bindingName, PmsProductEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }
}
