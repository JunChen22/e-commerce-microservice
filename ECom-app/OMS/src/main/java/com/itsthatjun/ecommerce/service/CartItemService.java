package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ConfirmOrderResult;
import com.itsthatjun.ecommerce.mbg.model.CartItem;
import io.swagger.annotations.ApiModelProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CartItemService {

    @ApiModelProperty(value = "get user's shopping cart")
    Flux<CartItem> getUserCart(int userId);

    @ApiModelProperty(value = "add an item to shopping cart")
    Flux<CartItem> addItem(CartItem item, int userId);

    @ApiModelProperty(value = "add all item to shopping cart")
    Flux<CartItem> addAllItem(List<CartItem> itemList, int userId);

    @ApiModelProperty(value = "update a item quantity in shopping cart")
    Flux<CartItem> updateQuantity(int cartItemId, int quantity, int userId);

    @ApiModelProperty(value = "delete an item in shopping cart")
    void deleteCartItem(int cartItemId, int userId);

    @ApiModelProperty(value = "clear shopping cart")
    void clearCartItem(int userId);
}
