package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {

    @ApiOperation("list current user's shopping cart")
    Flux<CartItem> list(int userId);

    @ApiOperation("add item to shopping cart")
    Mono<CartItem> add(CartItem cartItem, int userId);

    @ApiOperation("update shopping cart item quantity")
    Mono<CartItem> updateQuantity(CartItem cartItem, int userId);

    @ApiOperation("remove item from shopping cart")
    Mono<Void> delete(int cartItemId, int userId);

    @ApiOperation("clear user shopping cart")
    Mono<Void> clear(int userId);

}
