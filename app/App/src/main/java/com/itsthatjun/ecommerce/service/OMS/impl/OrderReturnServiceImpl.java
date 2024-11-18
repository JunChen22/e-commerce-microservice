package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.oms.ReturnParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnRequestDTO;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.OMS.OrderReturnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class OrderReturnServiceImpl implements OrderReturnService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final SecurityUtil securityUtil;

    private final String OMS_SERVICE_URL = "http://oms/order/return";

    @Autowired
    public OrderReturnServiceImpl(WebClient webClient, StreamBridge streamBridge, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.securityUtil = securityUtil;
    }

    public Mono<ReturnDetail> checkStatus(String orderSn) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = OMS_SERVICE_URL + "/status/" + orderSn;
                    LOG.debug("Will call the checkStatus API on URL: {}", url);

                    return webClient.get().uri(url)
                            .header("X-MemberId", String.valueOf(memberId))
                            .retrieve()
                            .bodyToMono(ReturnDetail.class)
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling OMS service: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<ReturnParam> applyForReturn(ReturnParam returnParam) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("return-out-0", new OmsReturnEvent(APPLY, memberId, returnParam))
                            .then(Mono.just(returnParam));
                });
    }

    @Override
    public Mono<ReturnParam> updateReturn(ReturnParam returnParam) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("return-out-0", new OmsReturnEvent(UPDATE, memberId, returnParam))
                            .then(Mono.just(returnParam));
        });
    }

    @Override
    public Mono<Void> cancelReturn(String orderSn) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    ReturnParam returnParam = new ReturnParam();
                    ReturnRequestDTO returnRequest = new ReturnRequestDTO();
                    returnRequest.setOrderSn(orderSn);
                    returnParam.setReturnRequest(returnRequest);
                    return sendMessage("return-out-0", new OmsReturnEvent(CANCEL, memberId, returnParam))
                            .then();
                });
    }

    private Mono<Void> sendMessage(String bindingName, OmsReturnEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
