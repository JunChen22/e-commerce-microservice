package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent;
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

import javax.servlet.http.HttpSession;

import static com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class OrderReturnController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms:8080/order/return";

    @Autowired
    public OrderReturnController(WebClient.Builder webClient, StreamBridge streamBridge,
                                 @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/all")
    @ApiOperation(value = "list all return request open waiting to be approved")
    public Flux<ReturnRequest> listAllReturnRequest(@RequestParam("statusCode") ReturnStatusCode statusCode){
        String url = OMS_SERVICE_URL + "/admin/all?statusCode=" + statusCode.getCode();

        return webClient.get().uri(url).retrieve().bodyToFlux(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "return a return request detail")
    public Mono<ReturnRequest> getReturnRequest(@PathVariable String serialNumber){
        String url = OMS_SERVICE_URL + "/admin/" + serialNumber;

        return webClient.get().uri(url).retrieve().bodyToMono(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/update")
    @ApiOperation(value = "update the status of the return apply")
    public void updateReturnOrderStatus(@RequestBody ReturnRequestDecision returnRequestDecision, HttpSession session){
        String operator = (String) session.getAttribute("adminName");

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

        Mono.fromRunnable(() -> sendMessage("return-out-0", event))
                .subscribeOn(publishEventScheduler).subscribe();
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
