package com.cropdeal.cart.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class AddToCartRequest {

    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Farmer ID is required")
    private Integer farmerId;
}
