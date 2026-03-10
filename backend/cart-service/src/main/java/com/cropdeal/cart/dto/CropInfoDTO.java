package com.cropdeal.cart.dto;

 import lombok.*;

 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 public class CropInfoDTO {
     private Long cartItemId; // <--- ADDED: This field was missing, causing map inference error
     private Long cropId;
     private String cropName;
     private Double cropPrice;
     private String imageUrl;
     private Integer quantity;
     private Integer farmerId;
     private String farmerName;
     private Double totalPrice;
 }