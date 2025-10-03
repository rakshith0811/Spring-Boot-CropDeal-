package com.cropdeal.cart.dto;

import com.cropdeal.cart.entity.CartItem;

public class AddToCartResponse {
    private CartItem cartItem;
    private String errorMessage;

    public AddToCartResponse(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public AddToCartResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
