package com.cropdeal.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryConfirmationEmailDTO {
    private String dealerEmail;
    private String dealerName;
    private String orderId;
    private String cropName;
    private int quantity;
    private double totalPrice;
    private String farmerName; // Include farmer name for context
}
