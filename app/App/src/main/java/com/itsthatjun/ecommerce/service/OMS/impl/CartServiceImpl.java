package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent;
import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import com.itsthatjun.ecommerce.service.OMS.CartService;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOG = LoggerFactory.getLogger(CartServiceImpl.class);

    private final UserServiceImpl userService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/cart";

    @Autowired
    public CartServiceImpl(UserServiceImpl userService, WebClient webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.userService = userService;
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<CartItemDTO> getUserCart() {
        int userId = userService.getUserId();
        String url = OMS_SERVICE_URL + "/list";
        LOG.debug("Will call the list API on URL: {}", url);
        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToFlux(CartItemDTO.class)
                .log(LOG.getName(), FINE).onErrorResume(WebClientResponseException.class, ex -> Flux.empty());
    }

    @Override
    public Mono<CartItemDTO> addItem(CartItemDTO cartItem) {
        return Mono.fromCallable(() -> {
            int userId = userService.getUserId();
            sendMessage("cart-out-0", new OmsCartEvent(ADD_ONE, userId, cartItem));
            return cartItem;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<CartItemDTO> updateQuantity(CartItemDTO cartItem) {
        return Mono.fromCallable(() -> {
            int userId = userService.getUserId();
            sendMessage("cart-out-0", new OmsCartEvent(UPDATE, userId, cartItem));
            return cartItem;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteCartItem(String cartItemSku) {
        return Mono.fromRunnable(() -> {
            int userId = userService.getUserId();
            CartItemDTO cartItem = new CartItemDTO();
            cartItem.setProductSku(cartItemSku);
            sendMessage("cart-out-0", new OmsCartEvent(DELETE, userId, cartItem));
        }).subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<Void> clearCart() {
        return Mono.fromRunnable(() -> {
            int userId = userService.getUserId();
            sendMessage("cart-out-0", new OmsCartEvent(CLEAR, userId, null));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsCartEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
