package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.service.SMS.CouponService;
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

import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String SMS_SERVICE_URL = "http://sms:8080/coupon";

    @Autowired
    public CouponServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<Coupon> listAll() {
        String url = SMS_SERVICE_URL+ "/admin/listAll";

        // TODO: add default value to get only active or disabled coupon , currently is all
        return webClient.get().uri(url).retrieve().bodyToFlux(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Coupon> list(int couponId) {
        String url = SMS_SERVICE_URL + "/admin/" + couponId;

        return webClient.get().uri(url).retrieve().bodyToMono(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Flux<Coupon> getCouponForProduct(int productId) {
        String url = SMS_SERVICE_URL + "/admin/product/" + productId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<CouponSale> create(CouponSale couponSale, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("coupon-out-0", new SmsAdminCouponEvent(CREATE_COUPON, couponSale, operator));
            return couponSale;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<CouponSale> update(CouponSale updatedCouponSale, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("coupon-out-0", new SmsAdminCouponEvent(UPDATE_COUPON, updatedCouponSale, operator));
            return updatedCouponSale;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> delete(int couponId, String operator) {
        return Mono.fromRunnable(() -> {
            CouponSale couponSale = new CouponSale();
            couponSale.setCouponId(couponId);
            sendMessage("coupon-out-0", new SmsAdminCouponEvent(DELETE_COUPON, couponSale, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, SmsAdminCouponEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
