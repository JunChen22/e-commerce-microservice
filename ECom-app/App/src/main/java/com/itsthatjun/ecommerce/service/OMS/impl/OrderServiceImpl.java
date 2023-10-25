package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.OMS.OrderService;
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

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent.Type.PAYMENT_FAILURE;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent.Type.PAYMENT_SUCCESS;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.CANCEL_ORDER;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.GENERATE_ORDER;
import static java.util.logging.Level.FINE;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    public static final String PAYPAL_SUCCESS_URL = "order/payment/success";

    private final String OMS_SERVICE_URL = "http://oms/order";

    @Autowired
    public OrderServiceImpl(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<Orders> detail(String orderSn, int userId) {
        String url = OMS_SERVICE_URL + "/detail/" + orderSn;
        LOG.debug("Will call the detail API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Flux<Orders> list(int status, Integer pageNum, Integer pageSize, int userId) {
        String url = OMS_SERVICE_URL + "/list";
        LOG.debug("Will call the list API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<OrderParam> generateOrder(OrderParam orderParam, String requestUrl, int userId) {
        return Mono.fromCallable(() -> {
            String successUrl = requestUrl + "/" + PAYPAL_SUCCESS_URL;
            String cancelUrl = requestUrl + "/";
            sendOrderMessage("order-out-0", new OmsOrderEvent(GENERATE_ORDER, userId, null, orderParam, successUrl, cancelUrl));
            return orderParam;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<String> successPay(String paymentId, String payerId) {
        return Mono.fromCallable(() -> {
            sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_SUCCESS, "", paymentId, payerId));
            return "payment success";
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> cancelUserOrder(String orderSn, int userId) {
        return Mono.fromRunnable(() -> {
            sendOrderMessage("order-out-0", new OmsOrderEvent(CANCEL_ORDER, userId, orderSn, null, null, null));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendOrderCompleteMessage(String bindingName, OmsCompletionEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendOrderMessage(String bindingName, OmsOrderEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
