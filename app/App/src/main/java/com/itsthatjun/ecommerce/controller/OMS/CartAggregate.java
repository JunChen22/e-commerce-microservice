package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import com.itsthatjun.ecommerce.service.OMS.impl.CartServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Cart controller", description = "Cart controller")
@RequestMapping("/cart")
public class CartAggregate {

    private final CartServiceImpl cartService;

    @Autowired
    public CartAggregate(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "list current user's shopping cart", description = "list current user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list current user's shopping cart"),
            @ApiResponse(responseCode = "404", description = "No items found")})
    @GetMapping(value = "/list")
    public Flux<CartItemDTO> list() {
        return cartService.getUserCart();
    }

    @Operation(summary = "add item to shopping cart", description = "add item to shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "add item to shopping cart"),
            @ApiResponse(responseCode = "404", description = "item not added")})
    @PostMapping(value = "/add")
    public Mono<CartItemDTO> add(@RequestBody CartItemDTO cartItem) {
        return cartService.addItem(cartItem);
    }

    @Operation(summary = "update shopping cart item quantity", description = "update shopping cart item quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update shopping cart item quantity"),
            @ApiResponse(responseCode = "404", description = "item not updated")})
    @PostMapping(value = "/update/quantity")
    public Mono<CartItemDTO> updateQuantity(@RequestBody CartItemDTO cartItem) {
        return cartService.updateQuantity(cartItem);
    }

    @Operation(summary = "remove item from shopping cart", description = "remove item from shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "remove item from shopping cart"),
            @ApiResponse(responseCode = "404", description = "item not removed")})
    @DeleteMapping(value = "/delete/{cartItemId}")
    public Mono<Void> delete(@PathVariable String cartItemSku) {
        return cartService.deleteCartItem(cartItemSku);
    }

    @Operation(summary = "clear user shopping cart", description = "clear user shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "clear user shopping cart"),
            @ApiResponse(responseCode = "404", description = "cart not cleared")})
    @DeleteMapping(value = "/clear")
    public Mono<Void> clear() {
        return cartService.clearCart();
    }
}
