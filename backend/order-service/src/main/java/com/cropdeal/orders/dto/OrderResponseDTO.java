package com.cropdeal.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String orderID;
    private String orderStatus;

    private String cropName;
    private Double cropPrice;
    private Integer cropQty;
    private int quantity;
    private String farmerName;
    private String farmerMobile;
    private String farmerAddress;

    private String dealerName;
    private String dealerMobile;
    private String dealerAddress;
    private Double totalPrice;
}
