package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "Return controller", description = "return order")
@RequestMapping("/order/return")
public class ReturnAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/order/return";

    public ReturnAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/status/{orderSn}")
    @ApiOperation(value = "check status of the return request")
    public Mono<ReturnRequest> checkStatus(@PathVariable String orderSn){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        String url = OMS_SERVICE_URL + "/status/" + orderSn;
        LOG.debug("Will call the checkStatus API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/apply")
    @ApiOperation(value = "Apply for return item, waiting for admin approve")
    public Mono<ReturnParam> applyForReturn(@RequestBody ReturnParam returnParam){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return Mono.fromCallable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(APPLY, userId, returnParam.getReturnRequest(), returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/update")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<ReturnParam> updateReturn(@RequestBody ReturnParam returnParam){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return Mono.fromCallable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(UPDATE, userId, null, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "change detail about return or return reason")
    public Mono<Void> cancelReturn(@RequestParam String orderSn){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrderSn(orderSn);

        return Mono.fromRunnable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(CANCEL, userId, returnRequest, null));
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
