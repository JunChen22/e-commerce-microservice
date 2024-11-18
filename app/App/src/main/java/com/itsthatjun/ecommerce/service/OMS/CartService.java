package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {

    /**
     * list current user's shopping cart
     * @return the cart item list
     */
    Flux<CartItemDTO> getUserCart();

    /**
     * add item to shopping cart
     * @param cartItem the cart item to add
     * @return the cart item added
     */
    Mono<CartItemDTO> addItem(CartItemDTO cartItem);

    /**
     * update shopping cart item quantity
     * @param cartItem the cart item to update
     * @return the cart item updated
     */
    Mono<CartItemDTO> updateQuantity(CartItemDTO cartItem);

    /**
     * remove item from shopping cart
     * @param cartItemSku the cart item sku to remove
     */
    Mono<Void> deleteCartItem(String cartItemSku);

    /**
     * clear user shopping cart
     */
    Mono<Void> clearCart();
}
