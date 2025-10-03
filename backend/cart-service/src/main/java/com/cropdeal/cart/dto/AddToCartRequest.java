package com.cropdeal.cart.dto;

 import lombok.Data;

 @Data
 public class AddToCartRequest {
     private Long cropId;
     private Integer quantity;
     private Integer farmerId; // <--- ADDED: Explicitly include farmerId here
 }