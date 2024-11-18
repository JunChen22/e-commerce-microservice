package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent;
import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.OMS.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCartEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOG = LoggerFactory.getLogger(CartServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final SecurityUtil securityUtil;

    private final String OMS_SERVICE_URL = "http://oms/cart";

    @Autowired
    public CartServiceImpl(WebClient webClient, StreamBridge streamBridge, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.securityUtil = securityUtil;
    }

    @Override
    public Flux<CartItemDTO> getUserCart() {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMapMany(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();

                    String url = OMS_SERVICE_URL + "/list";
                    LOG.debug("Will call the list API on URL: {}", url);

                    return webClient.get()
                            .uri(url)
                            .header("X-MemberId", String.valueOf(memberId))
                            .retrieve()
                            .bodyToFlux(CartItemDTO.class)
                            .timeout(Duration.ofSeconds(5))
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling CMS service: {}", ex.getMessage());
                                return Flux.empty();
                            });
                });
    }

    @Override
    public Mono<CartItemDTO> addItem(CartItemDTO cartItem) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("cart-out-0", new OmsCartEvent(ADD_ONE, memberId, cartItem))
                            .then(Mono.just(cartItem));
                });
    }

    @Override
    public Mono<CartItemDTO> updateQuantity(CartItemDTO cartItem) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("cart-out-0", new OmsCartEvent(UPDATE, memberId, cartItem))
                            .then(Mono.just(cartItem));
        });
    }

    @Override
    public Mono<Void> deleteCartItem(String cartItemSku) {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    CartItemDTO cartItem = new CartItemDTO();
                    cartItem.setProductSku(cartItemSku);
                    return sendMessage("cart-out-0", new OmsCartEvent(DELETE, memberId, cartItem))
                            .then();
        });
    }

    @Override
    public Mono<Void> clearCart() {
        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId())
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    return sendMessage("cart-out-0", new OmsCartEvent(CLEAR, memberId, null))
                            .then();
        });
    }

    private Mono<Void> sendMessage(String bindingName, OmsCartEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            streamBridge.send(bindingName, message);
        });
    }
}
