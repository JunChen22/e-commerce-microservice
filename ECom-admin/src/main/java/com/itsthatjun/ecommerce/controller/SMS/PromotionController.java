package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
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


import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/sale")
@Api(tags = "Sales related", description = "Item on sale for a set of time")
public class PromotionController {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Value("${app.SMS-service.host}")
    String salesServiceURL;
    @Value("${app.SMS-service.port}")
    int port;

    @Autowired
    public PromotionController(WebClient.Builder webClient, StreamBridge streamBridge,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/AllSale")
    @ApiOperation("All the sales that is going on")
    public Flux<PromotionSale> getAllPromotionSale() {
        String url = "http://" + salesServiceURL + ":" + port + "/sale/";

        return webClient.get().uri(url).retrieve().bodyToFlux(PromotionSale.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("Items that are on regular sale")
    public Flux<Product> getAllPromotionSaleItem() {
        String url = "http://" + salesServiceURL + ":" + port + "/sale/";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("Items that are like flash sale, clearance sale that needs to go quick")
    public Flux<Product> getAllFlashSaleItem() {

        String url = "http://" + salesServiceURL + ":" + port + "/sale/";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @PostMapping("/create")
    @ApiOperation("")
    public void createSale(@RequestBody OnSaleRequest request) {
        Mono.fromRunnable(() -> sendMessage("sales-out-0", new SmsAdminSaleEvent(CREATE_SALE, request)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation("")
    public void updateSale(@RequestBody OnSaleRequest request) {
        int promotionId = request.getPromotionId();
        Mono.fromRunnable(() -> sendMessage("sales-out-0", new SmsAdminSaleEvent(UPDATE_SALE, request)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{promotionSaleId}")
    @ApiOperation("")
    public void delete(@PathVariable int promotionSaleId) {
        OnSaleRequest saleRequest = new OnSaleRequest();
        saleRequest.setPromotionId(promotionSaleId);
        Mono.fromRunnable(() -> sendMessage("sales-out-0", new SmsAdminSaleEvent(DELETE_SALE, saleRequest)))
                .subscribeOn(publishEventScheduler).subscribe();
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
