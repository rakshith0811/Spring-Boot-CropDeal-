package com.cropdeal.orders.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Dealer ID is required")
    private Long dealerId;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotBlank(message = "Order status is required")
    private String orderStatus;
}
