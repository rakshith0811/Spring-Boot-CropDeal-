package com.cropdeal.cart.controller;

import com.cropdeal.cart.dto.AddToCartRequest;
import com.cropdeal.cart.dto.AddToCartResponse; // Optional: Only keep if it's being used elsewhere
import com.cropdeal.cart.dto.CropInfoDTO;
import com.cropdeal.cart.entity.CartItem;
import com.cropdeal.cart.service.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{dealerId}")
    public CartItem addToCart(@PathVariable Long dealerId, @RequestBody AddToCartRequest request) {
        return cartService.addToCart(dealerId, request.getCropId(), request.getQuantity(), request.getFarmerId());
    }

    /**
     * Retrieves all items in the dealer's cart.
     *
     * @param dealerId Dealer ID
     * @return List of CropInfoDTO
     */
    @GetMapping("/{dealerId}")
    public List<CropInfoDTO> getCart(@PathVariable Long dealerId) {
        return cartService.getCartItems(dealerId);
    }

    /**
     * Clears the cart for the given dealer.
     *
     * @param dealerId Dealer ID
     * @return Confirmation response
     */
    @Transactional
    @DeleteMapping("/clear/{dealerId}")
    public ResponseEntity<String> clearCart(@PathVariable Long dealerId) {
        cartService.clearCart(dealerId);
        return ResponseEntity.ok("Cart deleted successfully");
    }
}
