package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.service.impl.CartItemServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/cart")
@Api(tags = "shopping cart controller", description = "shopping cart and related api")
public class CartItemController {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemController.class);

    private final CartItemServiceImpl cartItemService;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    public CartItemController(CartItemServiceImpl cartItemService) {
        this.cartItemService = cartItemService;
    }

    @ApiOperation("list current user's shopping cart")
    @GetMapping(value = "/list")
    public Flux<CartItem> list(@RequestHeader("X-UserId") int userId) {
        return cartItemService.getUserCart(userId);
    }
}
