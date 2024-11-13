package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {

    /**
     * list current user's shopping cart
     */
    Flux<CartItemDTO> getUserCart();

    /**
     * add item to shopping cart
     */
    Mono<CartItemDTO> addItem(CartItemDTO cartItem);

    /**
     * update shopping cart item quantity
     */
    Mono<CartItemDTO> updateQuantity(CartItemDTO cartItem);

    /**
     * remove item from shopping cart
     */
    Mono<Void> deleteCartItem(String cartItemSku);

    /**
     * clear user shopping cart
     */
    Mono<Void> clearCart();
}
