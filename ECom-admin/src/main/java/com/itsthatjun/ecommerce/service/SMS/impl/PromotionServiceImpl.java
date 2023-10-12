package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.PromotionService;
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

import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String SMS_SERVICE_URL = "http://sms:8080/sale";

    @Autowired
    public PromotionServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                                @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<PromotionSale> getAllPromotionSale() {
        String url = SMS_SERVICE_URL + "/sale/AllSale";

        return webClient.get().uri(url).retrieve().bodyToFlux(PromotionSale.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> getAllPromotionSaleItem() {
        String url = SMS_SERVICE_URL + "/sale/AllPromotionSaleItem";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> getAllFlashSaleItem() {

        String url = SMS_SERVICE_URL + "/sale/AllFlashSaleItem";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<OnSaleRequest> createSale(OnSaleRequest request, String operator) {
        return Mono.fromCallable(() -> {
                sendMessage("sales-out-0", new SmsAdminSaleEvent(CREATE_SALE, request, operator));
                return request;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<OnSaleRequest> updateSaleInfo(OnSaleRequest request, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("sales-out-0", new SmsAdminSaleEvent(UPDATE_SALE_INFO, request, operator));
            return request;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<OnSaleRequest> updateSalePrice(OnSaleRequest request, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("sales-out-0", new SmsAdminSaleEvent(UPDATE_SALE_PRICE, request, operator));
            return request;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<OnSaleRequest> updateSaleStatus(OnSaleRequest request, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("sales-out-0", new SmsAdminSaleEvent(UPDATE_SALE_STATUS, request, operator));
            return request;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> delete(int promotionSaleId, String operator) {
        return Mono.fromRunnable(() -> {
            OnSaleRequest saleRequest = new OnSaleRequest();
            saleRequest.setPromotionId(promotionSaleId);
            sendMessage("sales-out-0", new SmsAdminSaleEvent(DELETE_SALE, saleRequest, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, SmsAdminSaleEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
