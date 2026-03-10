package com.cropdeal.mail.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmationEmailDTO {

    @NotBlank(message = "Dealer email is required")
    @Email(message = "Dealer email must be a valid email address")
    private String dealerEmail;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private double totalAmount;

    @NotEmpty(message = "Order items cannot be empty")
    private List<@Valid OrderItemDetail> items;

    @NotBlank(message = "Dealer name is required")
    private String dealerName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {

        @NotBlank(message = "Crop name is required")
        private String cropName;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        @DecimalMin(value = "0.0", inclusive = false, message = "Price per kg must be greater than 0")
        private double pricePerKg;

        @NotBlank(message = "Farmer name is required")
        private String farmerName;

        @DecimalMin(value = "0.0", inclusive = false, message = "Item total price must be greater than 0")
        private double itemTotalPrice;
    }
}
