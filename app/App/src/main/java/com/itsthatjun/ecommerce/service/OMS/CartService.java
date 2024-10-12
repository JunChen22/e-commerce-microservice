package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.model.CartItem;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {

    @ApiOperation("list current user's shopping cart")
    Flux<CartItem> list();

    @ApiOperation("add item to shopping cart")
    Mono<CartItem> add(CartItem cartItem);

    @ApiOperation("update shopping cart item quantity")
    Mono<CartItem> updateQuantity(CartItem cartItem);

    @ApiOperation("remove item from shopping cart")
    Mono<Void> delete(int cartItemId);

    @ApiOperation("clear user shopping cart")
    Mono<Void> clear();

}
