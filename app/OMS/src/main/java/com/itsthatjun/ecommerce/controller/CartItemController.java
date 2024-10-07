package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.service.impl.CartItemServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/cart")
@Tag(name = "shopping cart controller", description = "shopping cart and related api")
public class CartItemController {

    private final CartItemServiceImpl cartItemService;

    @Autowired
    public CartItemController(CartItemServiceImpl cartItemService) {
        this.cartItemService = cartItemService;
    }

    @ApiOperation("list current user's shopping cart")
    @GetMapping(value = "/list")
    public Flux<CartItem> list(@RequestHeader("X-UserId") int userId) {
        return cartItemService.getUserCart(userId);
    }
}
