package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.CartItemMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ShoppingCartMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.CartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final ShoppingCartMapper shoppingCartMapper;

    private final ProductSkuMapper skuMapper;

    private final CartItemMapper cartItemMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public CartItemServiceImpl(ShoppingCartMapper shoppingCartMapper, CartItemMapper cartItemMapper, ProductSkuMapper skuMapper,
                               @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.skuMapper = skuMapper;
        this.cartItemMapper = cartItemMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<CartItem> getUserCart(int userId) {
        return Mono.fromCallable(() -> {
            ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
            shoppingCartExample.createCriteria().andMemberIdEqualTo(userId);
            List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByExample(shoppingCartExample);

            ShoppingCart cart = shoppingCarts.get(0);
            CartItemExample example = new CartItemExample();
            example.createCriteria().andCartIdEqualTo(cart.getId());
            return cartItemMapper.selectByExample(example);
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<CartItem> addItem(CartItem item, int userId) {
       return Mono.fromCallable(() ->
           internalAddItem(item, userId)
       ).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    private List<CartItem> internalAddItem(CartItem newItem, int userId) {
        ShoppingCart currentShoppingCart  = getCurrentCart(userId);

        CartItemExample cartItemExample = new CartItemExample();
        cartItemExample.createCriteria().andCartIdEqualTo(currentShoppingCart.getId());
        List<CartItem> currentCartItems = cartItemMapper.selectByExample(cartItemExample);
        String productSkuCode = newItem.getProductSku();
        boolean existingInCart = false;

        for (CartItem item : currentCartItems) {
            if (item.getProductSku().equals(productSkuCode)) {
                int currentQuantity = item.getQuantity();
                item.setQuantity(currentQuantity + newItem.getQuantity());
                item.setModifyDate(new Date());
                cartItemMapper.updateByPrimaryKeySelective(item);
                existingInCart = true;
                break;
            }
        }

        if (!existingInCart) {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(productSkuCode);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) throw new RuntimeException("Sku code error, does not exist: " + productSkuCode);

            ProductSku sku = skuList.get(0);

            newItem.setCartId(currentShoppingCart.getId());
            newItem.setProductId(sku.getProductId());
            newItem.setPrice(sku.getPrice());
            newItem.setProductPic(sku.getPicture());
            newItem.setCreatedAt(new Date());
            cartItemMapper.insert(newItem);
        }

        // check quantity and price
        currentCartItems = cartItemMapper.selectByExample(cartItemExample);
        return currentCartItems;
    }

    @Override
    public Flux<CartItem> updateQuantity(int cartItemId, int newQuantity, int userId) {
        return Mono.fromCallable(() -> {
            ShoppingCart cart = getCurrentCart(userId);
            CartItem currentCartItem = cartItemMapper.selectByPrimaryKey(cartItemId);

            if (currentCartItem == null) throw new RuntimeException("cart item does not exist");

            currentCartItem.setQuantity(newQuantity);
            cartItemMapper.updateByPrimaryKey(currentCartItem);

            CartItemExample cartItemExample = new CartItemExample();
            cartItemExample.createCriteria().andCartIdEqualTo(cart.getId());
            List<CartItem> updatedCart = cartItemMapper.selectByExample(cartItemExample);
            return updatedCart;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteCartItem(int cartItemId, int userId) {
        return Mono.fromRunnable(() -> {
            ShoppingCart cart = getCurrentCart(userId);

            CartItemExample example = new CartItemExample();
            example.createCriteria().andCartIdEqualTo(cart.getId()).andIdEqualTo(cartItemId);
            List<CartItem> cartItemList = cartItemMapper.selectByExample(example);

            if (cartItemList.isEmpty()) throw new RuntimeException("Item does not exist");

            cartItemMapper.deleteByExample(example);
        }).subscribeOn(jdbcScheduler).then();
    }

    @Override
    public Mono<Void> clearCartItem(int userId) {
        return Mono.fromRunnable(() -> {
            ShoppingCart cart = getCurrentCart(userId);

            CartItemExample example = new CartItemExample();
            example.createCriteria().andCartIdEqualTo(cart.getId());
            cartItemMapper.deleteByExample(example);
        }).subscribeOn(jdbcScheduler).then();
    }

    private ShoppingCart getCurrentCart (int userId) {
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
        return currentShoppingCart;
    }
}
