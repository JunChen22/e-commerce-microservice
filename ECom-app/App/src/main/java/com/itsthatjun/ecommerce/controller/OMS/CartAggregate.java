package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent;
import com.itsthatjun.ecommerce.mbg.model.CartItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent.Type.ADD_ONE;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "Cart controller", description = "Cart controller")
@RequestMapping("/cart")
public class CartAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CartAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms";

    @Autowired
    public CartAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient, StreamBridge streamBridge,
                         @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @ApiOperation("list current user's shopping cart")
    @GetMapping(value = "/list")
    public Flux<CartItem> list(@RequestParam int userId) {
        String url = OMS_SERVICE_URL + "/cart/list?userId=" + userId;
        LOG.debug("Will call the list API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(CartItem.class)
                .log(LOG.getName(), FINE).onErrorResume(WebClientResponseException.class, ex -> empty());
    }

    @ApiOperation("add item to shopping cart")
    @PostMapping(value = "/add")
    public Mono<CartItem> add(@RequestBody CartItem cartItem, @RequestParam int userId) {
        return Mono.fromCallable(() -> {
            List<CartItem> cartItemList = new ArrayList<>();
            cartItemList.add(cartItem);
            sendMessage("cart-out-0", new OmsCartEvent(ADD_ONE, userId, cartItemList));
            return cartItem;
        }).subscribeOn(publishEventScheduler);
    }

    @ApiOperation("add all item to shopping cart")
    @PostMapping(value = "/add/all")
    public Flux<CartItem> addAll(@RequestBody List<CartItem> cartItem, @RequestParam int userId) {
        return Flux.defer(() -> {
                sendMessage("cart-out-0", new OmsCartEvent(OmsCartEvent.Type.ADD_ALL, userId, cartItem));
                return Flux.fromIterable(cartItem);
        }).subscribeOn(publishEventScheduler);
    }

    @ApiOperation("update shopping cart item quantity")
    @PostMapping(value = "/update/quantity")
    public Mono<Void> updateQuantity(@RequestParam int cartItemId, @RequestParam int quantity, @RequestParam int userId) {
        return Mono.fromRunnable(() ->
                sendMessage("cart-out-0", new OmsCartEvent(OmsCartEvent.Type.UPDATE, userId, null, quantity, cartItemId))
        ).subscribeOn(publishEventScheduler).then();
    }

    @ApiOperation("remove item from shopping cart")
    @DeleteMapping(value = "/delete/{cartItemId}")
    public Mono<Void> delete(@PathVariable int cartItemId, @RequestParam int userId) {
        return Mono.fromRunnable(() ->
                sendMessage("cart-out-0", new OmsCartEvent(OmsCartEvent.Type.DELETE, userId, null, 1, cartItemId))
        ).subscribeOn(publishEventScheduler).then();
    }

    @ApiOperation("clear user shopping cart")
    @DeleteMapping(value = "/clear")
    public Mono<Void> clear(@RequestParam int userId) {
        return Mono.fromRunnable(() ->
                sendMessage("cart-out-0", new OmsCartEvent(OmsCartEvent.Type.CLEAR, userId, null))
        ).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsCartEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    public Mono<Health> getOmsHealth() {
        return getHealth(OMS_SERVICE_URL);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}
