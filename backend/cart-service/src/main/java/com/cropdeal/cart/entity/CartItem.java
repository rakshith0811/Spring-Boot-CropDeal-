package com.cropdeal.cart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dealerId;
    private Long cropId;
    private String cropName;
    private Integer quantity;
    private Integer farmerId; 
    private Double price;
    private String imageUrl;
    private String farmerName;
    private Double totalPrice;
}
