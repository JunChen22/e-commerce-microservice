package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminCouponEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/coupon")
@Api(tags = "Coupon related", description = "CRUD coupon by admin with right roles/permission")
public class CouponController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Value("${app.SMS-service.host}")
    String salesServiceURL;
    @Value("${app.SMS-service.port}")
    int port;

    @Autowired
    public CouponController(WebClient.Builder webClient, StreamBridge streamBridge,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "return all working non-expired coupon")
    public Flux<Coupon> listAll() {
        String url = "http://" + salesServiceURL + ":" + port + "/coupon/";

        // TODO: add default value to get only active or disabled coupon , currently is all
        return webClient.get().uri(url).retrieve().bodyToFlux(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/{couponId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Mono<Coupon> list(@PathVariable int couponId) {
        String url = "http://" + salesServiceURL + ":" + port + "/coupon/" + couponId;

        return webClient.get().uri(url).retrieve().bodyToMono(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @GetMapping("/product/all/{productId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Flux<Coupon> getCouponForProduct(@PathVariable int productId) {
        String url = "http://" + salesServiceURL + ":" + port + "/coupon/product/all/" + productId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Coupon.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @PostMapping("/create")
    @ApiOperation(value = "create a coupon")
    public void create(@RequestBody CouponSale couponSale) {
        Mono.fromRunnable(() -> sendMessage("coupon-out-0", new SmsAdminCouponEvent(CREATE_COUPON, couponSale)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a coupon")
    public void update(@RequestBody CouponSale updatedCouponSale){
        Mono.fromRunnable(() -> sendMessage("coupon-out-0", new SmsAdminCouponEvent(UPDATE_COUPON, updatedCouponSale)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{couponId}")
    @ApiOperation(value = "delete a coupon")
    public void delete(@PathVariable int couponId) {
        CouponSale couponSale = new CouponSale();
        couponSale.setCouponId(couponId);
        Mono.fromRunnable(() -> sendMessage("coupon-out-0", new SmsAdminCouponEvent(DELETE_COUPON, couponSale)))
                .subscribeOn(publishEventScheduler).subscribe();
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
