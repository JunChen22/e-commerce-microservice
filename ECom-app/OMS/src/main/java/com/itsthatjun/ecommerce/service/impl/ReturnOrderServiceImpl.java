package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.ReturnDao;
import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsSalesStockOutEvent;
import com.itsthatjun.ecommerce.exceptions.ReturnRequestException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.itsthatjun.ecommerce.service.ReturnOrderService;
import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

    private final PaypalService paypalService;

    private final ReturnRequestMapper returnRequestMapper;

    private final ReturnReasonPicturesMapper picturesMapper;

    private final ReturnItemMapper returnItemMapper;

    private final OrderItemMapper orderItemMapper;

    private final OrdersMapper ordersMapper;

    private final CompanyAddressMapper companyAddressMapper;

    private final ReturnLogMapper logMapper;

    private final ReturnDao returnDao;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    @Autowired
    public ReturnOrderServiceImpl(PaypalService paypalService, ReturnRequestMapper returnRequestMapper, ReturnReasonPicturesMapper picturesMapper,
                                  ReturnItemMapper returnItemMapper, OrderItemMapper orderItemMapper, OrdersMapper ordersMapper,
                                  CompanyAddressMapper companyAddressMapper, ReturnLogMapper logMapper, ReturnDao returnDao, StreamBridge streamBridge,
                                  @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.paypalService = paypalService;
        this.returnRequestMapper = returnRequestMapper;
        this.picturesMapper = picturesMapper;
        this.returnItemMapper = returnItemMapper;
        this.orderItemMapper = orderItemMapper;
        this.ordersMapper = ordersMapper;
        this.companyAddressMapper = companyAddressMapper;
        this.logMapper = logMapper;
        this.returnDao = returnDao;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<ReturnRequest> getStatus(String orderSn, int userId) {
        return Mono.fromCallable( () -> {
                    ReturnRequest returnRquest = getUserReturnRequest(orderSn, userId);
                    return returnRquest;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ReturnRequest> applyForReturn(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList,
                                              Map<String, Integer> skuQuantity, int userId) {
        return Mono.fromCallable(() -> {
            ReturnRequest newReturnRequest = internalApplyForReturn(returnRequest, picturesList, skuQuantity, userId);
            return newReturnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    private ReturnRequest internalApplyForReturn(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList,
                                              Map<String, Integer> skuQuantity, int userId) {
        String orderSn = returnRequest.getOrderSn();
        returnRequest.setOrderSn(orderSn);
        returnRequest.setCreatedAt(new Date());
        returnRequest.setMemberId(userId);
        returnRequest.setStatus(0);

        returnRequestMapper.insert(returnRequest);

        int returnRequestId = returnRequest.getId();

        double returnAmount = 0;

        // check for item validation from order and price
        for (String sku: skuQuantity.keySet()) {
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderSnEqualTo(orderSn).andProductSkuCodeEqualTo(sku);
            List<OrderItem> orderItem = orderItemMapper.selectByExample(orderItemExample);

            if (orderItem.isEmpty()) throw new ReturnRequestException("Can not find order item with SKU code: " + sku);

            OrderItem item  = orderItem.get(0);
            int quantityToReturn = skuQuantity.get(sku);
            double purchasePrice = item.getRealAmount().doubleValue();

            ReturnItem returnItem = new ReturnItem();
            returnItem.setReturnRequestId(returnRequestId);
            returnItem.setOrderSn(orderSn);
            returnItem.setPurchasedPrice(BigDecimal.valueOf(purchasePrice));
            returnItem.setQuantity(quantityToReturn);
            returnItemMapper.insert(returnItem);

            returnAmount += purchasePrice * quantityToReturn;
        }

        returnRequest.setAskingAmount(BigDecimal.valueOf(returnAmount));

        // attach picture to request if any
        for (ReturnReasonPictures picture: picturesList) {
            picture.setCreatedAt(new Date());
            picture.setReturnRequestId(returnRequestId);
            picturesMapper.insert(picture);
        }
        return returnRequest;
    }

    @Override
    public Mono<ReturnRequest> updateReturnInfo(ReturnRequest returnRequest, List<ReturnReasonPictures> picturesList, String orderSn, int userId) {
        return Mono.fromCallable(() -> {
            ReturnRequest foundReturnRequest = getUserReturnRequest(orderSn, userId);

            foundReturnRequest.setUpdatedAt(new Date());

            // TODO: update the info

            return returnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    @Override // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3. 4 cancel
    public Mono<ReturnRequest> cancelReturn(String orderSn, int userId) {
        return Mono.fromCallable(() -> {
            ReturnRequest foundReturnRequest = getUserReturnRequest(orderSn, userId);
            foundReturnRequest.setStatus(4);
            foundReturnRequest.setUpdatedAt(new Date());
            returnRequestMapper.updateByPrimaryKey(foundReturnRequest);
            return foundReturnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    private ReturnRequest getUserReturnRequest(String orderSn, int userId) {
        ReturnRequestExample orderReturnRequestExample = new ReturnRequestExample();
        orderReturnRequestExample.createCriteria().andOrderSnEqualTo(orderSn).andMemberIdEqualTo(userId);
        List<ReturnRequest> orderReturnRequestList = returnRequestMapper.selectByExample(orderReturnRequestExample);

        if (orderReturnRequestList.isEmpty()) {
            throw new ReturnRequestException("Can't find return Request with order serial number: " + orderSn);
        } else {
            return  orderReturnRequestList.get(0);
        }
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @Override
    public Flux<ReturnRequest> getAllReturnRequest(int statusCode) {
        return Mono.fromCallable(() -> {
                ReturnRequestExample example = new ReturnRequestExample();
                example.createCriteria().andStatusEqualTo(statusCode);
                List<ReturnRequest> orderReturnRequests = returnRequestMapper.selectByExample(example);
                return orderReturnRequests;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ReturnDetail> getReturnDetail(String orderSn) {
        return Mono.fromCallable(() -> {
            ReturnDetail returnDetail = returnDao.getDetail(orderSn);
            return returnDetail;
        }).subscribeOn(jdbcScheduler);
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3
    @Override
    public Mono<ReturnRequest> approveReturnRequest(ReturnRequestDecision returnRequestDecision, String operator) {
        return Mono.fromCallable(() -> {
            ReturnRequest returnRequest= internalApproveReturnRequest(returnRequestDecision, operator);
            return returnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    private ReturnRequest internalApproveReturnRequest(ReturnRequestDecision returnRequestDecision, String operator) {
        ReturnRequest orderReturnRequest = returnRequestDecision.getReturnRequest();
        List<ReturnItem> returnItemList = returnRequestDecision.getReturnItemList();
        int companyAddressId = orderReturnRequest.getCompanyAddressId();
        String orderSn = orderReturnRequest.getOrderSn();

        // check price from their order
        double returnItemRefundAmount = 0;
        for (ReturnItem item: returnItemList) {
            String itemSku = item.getProductSku();
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andProductSkuCodeEqualTo(itemSku);
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

            if (orderItemList.isEmpty()) throw new RuntimeException("Return item not in order: " + orderSn);
            OrderItem orderItem = orderItemList.get(0);

            if (orderItem.getRealAmount().doubleValue() != item.getPurchasedPrice().doubleValue()) {
                throw new RuntimeException("Error pricing with return item");
            }

            int quantity = item.getQuantity();

            returnItemRefundAmount += item.getPurchasedPrice().doubleValue() * quantity;    // TODO: watch out for big decimal difference
        }

        /*  TODO: need to change double back to big decimal, this pricing is causing error
        if (returnItemRefundAmount != orderReturnRequest.getAskingAmount().doubleValue()) {
            throw new RuntimeException("Return order Request asking refund pricing error");
        }
         */

        orderReturnRequest.setStatus(1);
        orderReturnRequest.setHandleOperator(operator);
        orderReturnRequest.setUpdatedAt(new Date());
        returnRequestMapper.updateByPrimaryKey(orderReturnRequest);

        ReturnLog returnUpdateLog = new ReturnLog();
        returnUpdateLog.setAction("Approve return Request request");
        returnUpdateLog.setOperator(operator);
        returnUpdateLog.setCreatedAt(new Date());
        logMapper.insert(returnUpdateLog);

        // generate return label
        // using companyAddressId to get address
        CompanyAddressExample companyAddressExample = new CompanyAddressExample();
        companyAddressExample.createCriteria().andIdEqualTo(companyAddressId);
        List<CompanyAddress> addressList = companyAddressMapper.selectByExample(companyAddressExample);

        if (addressList.isEmpty()) throw new RuntimeException("Company address is invalid with company address id :" + companyAddressId);
        CompanyAddress receivedLocation = addressList.get(0);

        // TODO: UPS to print label and send back to customer

        // TODO: send notification to maybe an email service to notify user
        return orderReturnRequest;
    }

    @Override
    public Mono<ReturnRequest> rejectReturnRequest(ReturnRequestDecision returnRequestDecision, String reason, String operator) {
        return Mono.fromCallable(() -> {
            ReturnRequest returnRequest = returnRequestDecision.getReturnRequest();
            returnRequest.setStatus(3);
            returnRequest.setHandleNote(reason);
            returnRequest.setHandleOperator(operator);
            returnRequest.setUpdatedAt(new Date());
            returnRequestMapper.updateByPrimaryKey(returnRequest);

            ReturnLog returnUpdateLog = new ReturnLog();
            returnUpdateLog.setAction("Rejected return Request request");
            returnUpdateLog.setOperator(operator);
            returnUpdateLog.setCreatedAt(new Date());
            logMapper.insert(returnUpdateLog);

            return returnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ReturnRequest> completeReturnRequest(ReturnRequestDecision returnRequestDecision, String operator) {
        return Mono.fromCallable(() -> {
            ReturnRequest returnRequest = internalCompleteReturnRequest(returnRequestDecision, operator);
            return  returnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    private ReturnRequest internalCompleteReturnRequest(ReturnRequestDecision returnRequestDecision, String operator) {
        ReturnRequest returnRequest = returnRequestDecision.getReturnRequest();

        String orderSn = returnRequest.getOrderSn();
        int returnRequestId = returnRequest.getId();

        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);

        if (ordersList.isEmpty()) throw new ReturnRequestException("Order does not exist error");
        Orders foundOrder = ordersList.get(0);

        returnRequest.setStatus(2);
        returnRequest.setHandleOperator(operator);
        returnRequest.setUpdatedAt(new Date());
        returnRequestMapper.updateByPrimaryKey(returnRequest);

        ReturnLog returnUpdateLog = new ReturnLog();
        returnUpdateLog.setAction("accepted return Request request");
        returnUpdateLog.setOperator(operator);
        returnUpdateLog.setCreatedAt(new Date());
        logMapper.insert(returnUpdateLog);

        ReturnItemExample returnItemExample = new ReturnItemExample();
        returnItemExample.createCriteria().andReturnRequestIdEqualTo(returnRequestId).andOrderSnEqualTo(orderSn);
        List<ReturnItem> returnedItemList = returnItemMapper.selectByExample(returnItemExample);

        // Get returned item sku and quantity for update SMS and PMS service.
        Map<String, Integer> skuQuantity = new HashMap<>();
        for (ReturnItem returnItem : returnedItemList) {
            String skuCode = returnItem.getProductSku();
            int quantity = returnItem.getQuantity();
            skuQuantity.put(skuCode, quantity);
        }

        sendProductStockUpdateMessage("product-out-0", new PmsProductOutEvent(PmsProductOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));
        sendSalesStockUpdateMessage("salesStock-out-0", new SmsSalesStockOutEvent(SmsSalesStockOutEvent.Type.UPDATE_RETURN, orderSn, skuQuantity));

        String paymentId = foundOrder.getPaymentId();
        double refundAmount = returnRequest.getAskingAmount().doubleValue();

        try {
            Refund refund = paypalService.createRefund(paymentId, refundAmount);
            // TODO: add refund info to database
            System.out.println("Refund successful!");
        } catch (PayPalRESTException e) {
            // Handle refund failure
            System.out.println("Refund failed: " + e.getMessage());
        }

        return returnRequest;
    }

    private void sendProductStockUpdateMessage(String bindingName, PmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }
}
