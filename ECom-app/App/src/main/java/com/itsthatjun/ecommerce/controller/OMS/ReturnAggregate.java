package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "Return controller", description = "return order")
@RequestMapping("/return")
public class ReturnAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms";

    public ReturnAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/status")
    @ApiOperation(value = "check status of the return request")
    public Mono<ReturnRequest> checkStatus(@RequestParam String orderSn, @RequestParam int userId){
        String url = OMS_SERVICE_URL + "/order/return?orderSn=" + orderSn + "&userId=" + userId;
        LOG.debug("Will call the checkStatus API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/apply")
    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    public Mono<ReturnParam> applyForReturn(@RequestBody ReturnParam returnParam, @RequestParam int userId){
        return Mono.fromCallable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(APPLY, userId, null, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/update")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<ReturnParam> updateReturn(@RequestBody ReturnParam returnParam, @RequestParam int userId){
        return Mono.fromCallable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(UPDATE, userId, null, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<Void> cancelReturn(@RequestParam String orderSn, @RequestParam int userId){
        ReturnParam returnParam = new ReturnParam();
        // TODO: fix this
        return Mono.fromRunnable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(CANCEL, userId, null, returnParam));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsReturnEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
