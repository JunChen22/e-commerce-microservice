package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.OMS.OrderReturnService;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class OrderReturnServiceImpl implements OrderReturnService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnServiceImpl.class);

    private final AdminServiceImpl adminService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/order/return";

    @Autowired
    public OrderReturnServiceImpl(AdminServiceImpl adminService, WebClient.Builder webClient, StreamBridge streamBridge,
                                  @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.adminService = adminService;
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<ReturnRequest> listAllReturnRequest(ReturnStatusCode statusCode) {
        String url = OMS_SERVICE_URL + "/admin/all?statusCode=" + statusCode.getCode();

        return webClient.get().uri(url).retrieve().bodyToFlux(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<ReturnDetail> listUserAllReturnRequest(int userId) {
        String url = OMS_SERVICE_URL + "/admin/user/all/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(ReturnDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ReturnDetail> getReturnRequest(String serialNumber) {
        String url = OMS_SERVICE_URL + "/admin/" + serialNumber;

        return webClient.get().uri(url).retrieve().bodyToMono(ReturnDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<Void> updateReturnOrderStatus(ReturnRequestDecision returnRequestDecision) {
        String operator = adminService.getAdminName();
        OmsAdminOrderReturnEvent event;

        switch (returnRequestDecision.getStatus()) {
            case APPROVED:
                event = new OmsAdminOrderReturnEvent(APPROVED, returnRequestDecision, operator);
                break;

            case REJECTED:
                event = new OmsAdminOrderReturnEvent(REJECTED, returnRequestDecision, operator);
                break;

            case COMPLETED_RETURN:
                event = new OmsAdminOrderReturnEvent(COMPLETED_RETURN, returnRequestDecision, operator);
                break;

            default:
                throw new IllegalArgumentException("Invalid status");
        }

        return Mono.fromRunnable(() -> {
            sendMessage("return-out-0", event);
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsAdminOrderReturnEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
