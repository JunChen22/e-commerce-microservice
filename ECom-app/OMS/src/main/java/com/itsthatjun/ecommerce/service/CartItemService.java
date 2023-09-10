package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CartItemService {

    @ApiOperation(value = "get user's shopping cart")
    Flux<CartItem> getUserCart(int userId);

    @ApiOperation(value = "add an item to shopping cart")
    Flux<CartItem> addItem(CartItem item, int userId);

    @ApiOperation(value = "add all item to shopping cart")
    Flux<CartItem> addAllItem(List<CartItem> itemList, int userId);

    @ApiOperation(value = "update a item quantity in shopping cart")
    Flux<CartItem> updateQuantity(int cartItemId, int quantity, int userId);

    @ApiOperation(value = "delete an item in shopping cart")
    void deleteCartItem(int cartItemId, int userId);

    @ApiOperation(value = "clear shopping cart")
    void clearCartItem(int userId);
}
