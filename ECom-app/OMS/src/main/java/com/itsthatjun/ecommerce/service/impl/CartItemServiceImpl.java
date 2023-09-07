package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ConfirmOrderResult;
import com.itsthatjun.ecommerce.mbg.mapper.CartItemMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ShoppingCartMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final ShoppingCartMapper shoppingCartMapper;

    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(ShoppingCartMapper shoppingCartMapper, CartItemMapper cartItemMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    public Flux<CartItem> getUserCart(int userId) {

        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByExample(shoppingCartExample);

        if (shoppingCarts.isEmpty()) {
            // Return an empty cart representation
            return Flux.fromIterable(Collections.emptyList());
        }

        ShoppingCart cart = shoppingCarts.get(0);
        CartItemExample example = new CartItemExample();
        example.createCriteria().andCartIdEqualTo(cart.getId());
        List<CartItem> shoppingCartItemList = cartItemMapper.selectByExample(example);

        return Flux.fromIterable(shoppingCartItemList);
    }


    @Override
    public Flux<CartItem> addItem(CartItem item, int userId) {

        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByExample(shoppingCartExample);
        ShoppingCart currentShoppingCart;

        // doesn't have shopping cart in database yet
        if (shoppingCarts.isEmpty()) {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setMemberId(userId);
            shoppingCartMapper.insert(newCart);
            currentShoppingCart = newCart;
        } else {
            currentShoppingCart = shoppingCarts.get(0);
        }

        // TODO: if exist then increase quantity but sku might be different

        item.setCartId(currentShoppingCart.getId());
        item.setCreatedAt(new Date());
        cartItemMapper.insert(item);

        CartItemExample cartItemExample = new CartItemExample();
        cartItemExample.createCriteria().andCartIdEqualTo(currentShoppingCart.getId());

        return Mono.fromCallable(() -> cartItemMapper.selectByExample(cartItemExample))
                .flatMapMany(Flux::fromIterable)
                .onErrorResume(e -> Flux.empty()); // Handle errors gracefully;
    }

    @Override
    public Flux<CartItem> addAllItem(List<CartItem> itemList, int userId) {

        for(CartItem item: itemList) {
            addItem(item, userId);
        }

        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);

        ShoppingCart currentShoppingCart  = shoppingCartMapper.selectByExample(shoppingCartExample).get(0);

        CartItemExample cartItemExample = new CartItemExample();
        cartItemExample.createCriteria().andCartIdEqualTo(currentShoppingCart.getId());

        return Mono.fromCallable(() -> cartItemMapper.selectByExample(cartItemExample))
                .flatMapMany(Flux::fromIterable)
                .onErrorResume(e -> Flux.empty());
    }


    @Override
    public Flux<CartItem> updateQuantity(int cartItemId, int quantity, int userId) {

        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);
        ShoppingCart cart = shoppingCartMapper.selectByExample(shoppingCartExample).get(0);

        CartItemExample example = new CartItemExample();
        example.createCriteria().andCartIdEqualTo(cart.getId()).andIdEqualTo(cartItemId);
        CartItem updated = cartItemMapper.selectByPrimaryKey(cartItemId);
        updated.setQuantity(quantity);
        cartItemMapper.updateByExampleSelective(updated, example);

        example.clear();
        example.createCriteria().andCartIdEqualTo(cart.getId());

        List<CartItem> updatedCart = cartItemMapper.selectByExample(example);

        return Flux.fromIterable(updatedCart);
    }

    @Override
    public void deleteCartItem(int cartItemId, int userId) {
        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);
        ShoppingCart cart = shoppingCartMapper.selectByExample(shoppingCartExample).get(0);

        CartItemExample example = new CartItemExample();
        example.createCriteria().andCartIdEqualTo(cart.getId()).andIdEqualTo(cartItemId);
        cartItemMapper.deleteByExample(example);
    }

    @Override
    public void clearCartItem(int userId) {
        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);
        ShoppingCart cart = shoppingCartMapper.selectByExample(shoppingCartExample).get(0);

        CartItemExample example = new CartItemExample();
        example.createCriteria().andCartIdEqualTo(cart.getId());
        cartItemMapper.deleteByExample(example);
    }
}
