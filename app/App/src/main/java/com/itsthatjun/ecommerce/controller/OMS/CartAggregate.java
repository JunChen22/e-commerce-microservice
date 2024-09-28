package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.service.OMS.impl.CartServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "Cart controller", description = "Cart controller")
@RequestMapping("/cart")
public class CartAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CartAggregate.class);

    private final CartServiceImpl cartService;

    @Autowired
    public CartAggregate(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @ApiOperation("list current user's shopping cart")
    @GetMapping(value = "/list")
    public Flux<CartItem> list() {
        return cartService.list();
    }

    @ApiOperation("add item to shopping cart")
    @PostMapping(value = "/add")
    public Mono<CartItem> add(@RequestBody CartItem cartItem) {
        return cartService.add(cartItem);
    }

    @ApiOperation("update shopping cart item quantity")
    @PostMapping(value = "/update/quantity")
    public Mono<CartItem> updateQuantity(@RequestBody CartItem cartItem) {
        return cartService.updateQuantity(cartItem);
    }

    @ApiOperation("remove item from shopping cart")
    @DeleteMapping(value = "/delete/{cartItemId}")
    public Mono<Void> delete(@PathVariable int cartItemId) {
        return cartService.delete(cartItemId);
    }

    @ApiOperation("clear user shopping cart")
    @DeleteMapping(value = "/clear")
    public Mono<Void> clear() {
        return cartService.clear();
    }
}
