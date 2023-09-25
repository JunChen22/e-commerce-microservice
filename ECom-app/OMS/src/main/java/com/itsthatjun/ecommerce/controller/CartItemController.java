package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.service.impl.CartItemServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "shopping cart controller", description = "shopping cart and related api")
@RequestMapping("/cart")
public class CartItemController {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemController.class);

    private final Scheduler jdbcScheduler;

    private final CartItemServiceImpl cartItemService;

    @Autowired
    public CartItemController(@Qualifier("jdbcScheduler") Scheduler jdbcScheduler, CartItemServiceImpl cartItemService) {
        this.jdbcScheduler = jdbcScheduler;
        this.cartItemService = cartItemService;
    }

    @ApiOperation("list current user's shopping cart")
    @GetMapping(value = "/list")
    public Flux<CartItem> list(@RequestHeader("X-UserId") int userId) {
        return cartItemService.getUserCart(userId);
    }

    @ApiOperation("add item to shopping cart")
    @PostMapping(value = "/add")
    public Flux<CartItem> add(@RequestBody CartItem cartItem, @RequestParam int userId) {
        return cartItemService.addItem(cartItem, userId);
    }

    @ApiOperation("add all item to shopping cart")
    @PostMapping(value = "/add/all")
    public Flux<CartItem> addAll(@RequestBody List<CartItem> cartItem, @RequestParam int userId) {
        return cartItemService.addAllItem(cartItem, userId);
    }

    @ApiOperation("update shopping cart item quantity")
    @PostMapping(value = "/update/quantity")
    public Flux<CartItem> updateQuantity(@RequestParam int cartItemId, @RequestParam int quantity, @RequestParam int userId) {
        return cartItemService.updateQuantity(cartItemId, quantity, userId);
    }

    @ApiOperation("remove item from shopping cart")
    @DeleteMapping(value = "/delete/{cartItemId}")
    public void delete(@PathVariable int cartItemId, @RequestParam int userId) {
        cartItemService.deleteCartItem(cartItemId, userId);
    }

    @ApiOperation("clear user shopping cart")
    @DeleteMapping(value = "/clear")
    public void clear(@RequestParam int userId) {
        cartItemService.clearCartItem(userId);
    }
}
