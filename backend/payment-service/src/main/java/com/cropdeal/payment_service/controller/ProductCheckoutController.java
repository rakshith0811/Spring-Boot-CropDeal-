package com.cropdeal.payment_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropdeal.payment_service.dto.ProductRequest;
import com.cropdeal.payment_service.dto.StripeResponse;
import com.cropdeal.payment_service.service.StripeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/payment/")
public class ProductCheckoutController {
	private StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@Valid @RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }

}
