package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.OMS.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent.Type.PAYMENT_SUCCESS;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.CANCEL_ORDER;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.GENERATE_ORDER;
import static java.util.logging.Level.FINE;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final SecurityUtil securityUtil;

    public static final String PAYPAL_SUCCESS_URL = "order/payment/success";

    private final String OMS_SERVICE_URL = "http://oms/order";

    @Autowired
    public OrderServiceImpl(WebClient webClient, StreamBridge streamBridge, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.securityUtil = securityUtil;
    }

    @Override
    public Mono<OrderDetail> detail(String orderSn) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = OMS_SERVICE_URL + "/detail/" + orderSn;
                    LOG.debug("Will call the detail API on URL: {}", url);

                    return webClient.get().uri(url)
                            .header("X-MemberId", String.valueOf(memberId))
                            .retrieve()
                            .bodyToMono(OrderDetail.class)
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling OMS service: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Flux<OrderDTO> list(int status, Integer pageNum, Integer pageSize) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMapMany(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = OMS_SERVICE_URL + "/list";
                    LOG.debug("Will call the list API on URL: {}", url);

                    return webClient.get().uri(url)
                            .header("X-MemberId", String.valueOf(memberId))
                            .retrieve().bodyToFlux(OrderDTO.class)
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling OMS service: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<OrderParam> generateOrder(OrderParam orderParam, String requestUrl) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String successUrl = requestUrl + "/" + PAYPAL_SUCCESS_URL;
                    String cancelUrl = requestUrl + "/";
                    return sendOrderMessage("order-out-0", new OmsOrderEvent(GENERATE_ORDER, memberId, null, orderParam, successUrl, cancelUrl))
                            .then(Mono.just(orderParam));
                });
    }

    @Override
    public Mono<String> getPaymentLink(String orderSn) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = OMS_SERVICE_URL + "/payment/getPaymentLink/" + orderSn;
                    LOG.debug("Will call the payment API on URL: {}", url);

                    return webClient.get().uri(url).header("X-MemberId", String.valueOf(memberId)).retrieve().bodyToMono(String.class)
                            .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
                });
    }

    @Override
    public Mono<String> successPay(String paymentId, String payerId) {
        return sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_SUCCESS, "", paymentId, payerId))
                .then(Mono.just("Payment success"));
    }

    @Override
    public Mono<Void> cancelUserOrder(String orderSn) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendOrderMessage("order-out-0", new OmsOrderEvent(CANCEL_ORDER, memberId, orderSn, null, null, null))
                            .then();
                });
    }

    private Mono<Void> sendOrderCompleteMessage(String bindingName, OmsCompletionEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }

    private Mono<Void> sendOrderMessage(String bindingName, OmsOrderEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
