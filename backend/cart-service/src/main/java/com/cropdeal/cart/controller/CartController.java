package com.cropdeal.cart.controller;

import com.cropdeal.cart.dto.AddToCartRequest;
import com.cropdeal.cart.dto.AddToCartResponse; 
import com.cropdeal.cart.dto.CropInfoDTO;
import com.cropdeal.cart.entity.CartItem;
import com.cropdeal.cart.service.CartService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{dealerId}")
    public CartItem addToCart(@PathVariable Long dealerId,@Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(dealerId, request.getCropId(), request.getQuantity(), request.getFarmerId());
    }
    @GetMapping("/{dealerId}")
    public List<CropInfoDTO> getCart(@PathVariable Long dealerId) {
        return cartService.getCartItems(dealerId);
    }
    @Transactional
    @DeleteMapping("/clear/{dealerId}")
    public ResponseEntity<String> clearCart(@PathVariable Long dealerId) {
        cartService.clearCart(dealerId);
        return ResponseEntity.ok("Cart deleted successfully");
    }
}
