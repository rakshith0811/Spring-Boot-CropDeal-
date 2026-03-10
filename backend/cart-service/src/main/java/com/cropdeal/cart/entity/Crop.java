package com.cropdeal.cart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "crop")
@Data
public class Crop {

    @Id
    private Long id;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "crop_price")
    private Double cropPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "crop_qty")
    private Integer cropQty;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;
}
