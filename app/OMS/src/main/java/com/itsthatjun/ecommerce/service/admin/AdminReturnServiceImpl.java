package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.dao.ReturnDao;
import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsReturnEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsSalesStockOutEvent;
import com.itsthatjun.ecommerce.exceptions.ReturnRequestException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.AdminReturnService;
import com.itsthatjun.ecommerce.service.PaypalService;
import com.itsthatjun.ecommerce.service.impl.ReturnOrderServiceImpl;
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
import java.util.*;

import static com.itsthatjun.ecommerce.dto.event.outgoing.OmsReturnEvent.Type.NEW_RETURN;
import static com.itsthatjun.ecommerce.dto.event.outgoing.OmsReturnEvent.Type.RETURN_UPDATE;

@Service
public class AdminReturnServiceImpl implements AdminReturnService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

    private final PaypalService paypalService;

    private final ReturnRequestMapper returnRequestMapper;

    private final ReturnReasonPicturesMapper picturesMapper;

    private final ReturnItemMapper returnItemMapper;

    private final OrderItemMapper orderItemMapper;

    private final OrdersMapper ordersMapper;

    private final CompanyAddressMapper companyAddressMapper;

    private final ReturnLogMapper logMapper;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final ReturnDao returnDao;

    @Autowired
    public AdminReturnServiceImpl(PaypalService paypalService, ReturnRequestMapper returnRequestMapper, ReturnReasonPicturesMapper picturesMapper,
                                  ReturnItemMapper returnItemMapper, OrderItemMapper orderItemMapper, OrdersMapper ordersMapper,
                                  CompanyAddressMapper companyAddressMapper, ReturnLogMapper logMapper, StreamBridge streamBridge,
                                  @Qualifier("jdbcScheduler") Scheduler jdbcScheduler, ReturnDao returnDao) {
        this.paypalService = paypalService;
        this.returnRequestMapper = returnRequestMapper;
        this.picturesMapper = picturesMapper;
        this.returnItemMapper = returnItemMapper;
        this.orderItemMapper = orderItemMapper;
        this.ordersMapper = ordersMapper;
        this.companyAddressMapper = companyAddressMapper;
        this.logMapper = logMapper;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
        this.returnDao = returnDao;
    }
    @Override
    public Flux<ReturnDetail> getUserAllReturnDetail(int userId) {
        return Mono.fromCallable(() -> {
            ReturnRequestExample returnRequestExample = new ReturnRequestExample();
            returnRequestExample.createCriteria().andMemberIdEqualTo(userId);
            List<ReturnRequest> returnRequestList = returnRequestMapper.selectByExample(returnRequestExample);
            if (returnRequestList.isEmpty()) return null;

            List<ReturnDetail> returnDetailList = new ArrayList<>();
            for (ReturnRequest returnRequest : returnRequestList) {
                int returnRequestId = returnRequest.getId();

                ReturnItemExample returnItemExample = new ReturnItemExample();
                returnItemExample.createCriteria().andReturnRequestIdEqualTo(returnRequestId);
                List<ReturnItem> returnItemList = returnItemMapper.selectByExample(returnItemExample);

                ReturnReasonPicturesExample picturesExample = new ReturnReasonPicturesExample();
                picturesExample.createCriteria().andReturnRequestIdEqualTo(returnRequestId);
                List<ReturnReasonPictures> returnReasonPicturesList = picturesMapper.selectByExample(picturesExample);

                ReturnDetail returnDetail = new ReturnDetail();
                //returnDetail.setReturnRequest(returnRequest); TODO:
                //returnDetail.setReturnItemList(returnItemList);
                //returnDetail.setPicturesList(returnReasonPicturesList);

                returnDetailList.add(returnDetail);
            }

            return returnDetailList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ReturnDetail> getReturnDetail(String orderSn) {
        return Mono.fromCallable(() -> {
            //ReturnDetail returnDetail = returnDao.getDetail(orderSn); TODO: fix dao
            ReturnRequestExample returnRequestExample = new ReturnRequestExample();
            returnRequestExample.createCriteria().andOrderSnEqualTo(orderSn);
            List<ReturnRequest> returnRequestList = returnRequestMapper.selectByExample(returnRequestExample);

            if (returnRequestList.isEmpty()) return null;


            ReturnRequest returnRequest = returnRequestList.get(0);
            ReturnDetail returnDetail = new ReturnDetail();
            /* TODO:
            int returnRequestId = returnRequest.getId();

            ReturnItemExample returnItemExample = new ReturnItemExample();
            returnItemExample.createCriteria().andReturnRequestIdEqualTo(returnRequestId);
            List<ReturnItem> returnItemList = returnItemMapper.selectByExample(returnItemExample);

            ReturnReasonPicturesExample picturesExample = new ReturnReasonPicturesExample();
            picturesExample.createCriteria().andReturnRequestIdEqualTo(returnRequestId);
            List<ReturnReasonPictures> returnReasonPicturesList = picturesMapper.selectByExample(picturesExample);

            returnDetail.setReturnRequest(returnRequest);
            returnDetail.setReturnItemList(returnItemList);
            returnDetail.setPicturesList(returnReasonPicturesList);
            */
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
        BigDecimal returnItemRefundAmount = BigDecimal.ZERO;

        for (ReturnItem item: returnItemList) {
            String itemSku = item.getProductSku();
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andProductSkuCodeEqualTo(itemSku);
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

            if (orderItemList.isEmpty()) throw new RuntimeException("Return item not in order: " + orderSn);
            OrderItem orderItem = orderItemList.get(0);

            if (orderItem.getRealAmount().compareTo(item.getPurchasedPrice()) != 0) {
                throw new RuntimeException("Error pricing with return item");
            }

            int quantity = item.getQuantity();

            returnItemRefundAmount = item.getPurchasedPrice().multiply(BigDecimal.valueOf(quantity)).add(returnItemRefundAmount);
        }

        // TODO: need to change double back to big decimal, this pricing is causing error
        if (returnItemRefundAmount.compareTo(orderReturnRequest.getAskingAmount()) != 0) {
            throw new RuntimeException("Return order Request asking refund pricing error");
        }

        orderReturnRequest.setStatus(1);
        orderReturnRequest.setHandleOperator(operator);
        orderReturnRequest.setUpdatedAt(new Date());
        returnRequestMapper.updateByPrimaryKey(orderReturnRequest);

        createUpdateLog(orderReturnRequest.getId(), "Approve return Request request", operator);

        // generate return label
        // using companyAddressId to get address
        CompanyAddressExample companyAddressExample = new CompanyAddressExample();
        companyAddressExample.createCriteria().andIdEqualTo(companyAddressId);
        List<CompanyAddress> addressList = companyAddressMapper.selectByExample(companyAddressExample);

        if (addressList.isEmpty()) throw new RuntimeException("Company address is invalid with company address id :" + companyAddressId);
        CompanyAddress receivedLocation = addressList.get(0);

        // TODO: UPS to print label and send back to customer

        // find order and get email and name
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andOrderSnEqualTo(orderReturnRequest.getOrderSn());
        Orders order = ordersMapper.selectByExample(ordersExample).get(0);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(order.getMemberEmail());
        userInfo.setName(order.getReceiverName());

        ReturnDetail returnDetail = returnDao.getReturnForNotification(orderSn);
        OmsReturnEvent event = new OmsReturnEvent(NEW_RETURN, returnDetail, userInfo);
        sendReturnUpdateMessage("orderReturnMessage-out-0", event);
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
            returnRequestMapper.updateByPrimaryKeySelective(returnRequest);

            createUpdateLog(returnRequest.getId(), "Rejected return Request request", operator);

            ReturnDetail returnDetail = returnDao.getReturnForNotification(returnRequest.getOrderSn());

            // find order and get email and name
            OrdersExample ordersExample = new OrdersExample();
            ordersExample.createCriteria().andOrderSnEqualTo(returnRequest.getOrderSn());
            Orders order = ordersMapper.selectByExample(ordersExample).get(0);

            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(order.getMemberEmail());
            userInfo.setName(order.getReceiverName());

            OmsReturnEvent event = new OmsReturnEvent(RETURN_UPDATE, returnDetail, userInfo);
            sendReturnUpdateMessage("orderReturnMessage-out-0", event);

            return returnRequest;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ReturnRequest> completeReturnRequest(ReturnRequestDecision returnRequestDecision, String operator) {
        return Mono.fromCallable(() -> {
            ReturnRequest returnRequest = internalCompleteReturnRequest(returnRequestDecision, operator);
            return returnRequest;
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

        createUpdateLog(returnRequestId, "accepted return Request request", operator);

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
        BigDecimal refundAmount = returnRequest.getAskingAmount();

        try {
            Refund refund = paypalService.createRefund(paymentId, refundAmount);
            // TODO: add refund info to database
            System.out.println("Refund successful!");
        } catch (PayPalRESTException e) {
            // Handle refund failure
            System.out.println("Refund failed: " + e.getMessage());
        }

        // find order and get email and name
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(foundOrder.getMemberEmail());
        userInfo.setName(foundOrder.getReceiverName());

        ReturnDetail returnDetail = returnDao.getReturnForNotification(returnRequest.getOrderSn());
        OmsReturnEvent event = new OmsReturnEvent(RETURN_UPDATE, returnDetail, userInfo);
        sendReturnUpdateMessage("orderReturnMessage-out-0", event);

        return returnRequest;
    }

    private void createUpdateLog(int returnRequestId, String updateAction, String operator) {
        ReturnLog log = new ReturnLog();
        log.setReturnRequestId(returnRequestId);
        log.setUpdateAction(updateAction);
        log.setOperator(operator);
        logMapper.insert(log);
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

    private void sendProductStockUpdateMessage(String bindingName, PmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsSalesStockOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendReturnUpdateMessage(String bindingName, OmsReturnEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
