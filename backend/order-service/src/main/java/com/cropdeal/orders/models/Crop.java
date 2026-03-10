package com.cropdeal.orders.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "crop")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crop_description")
    private String cropDescription;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "crop_price")
    private Double cropPrice;

    @Column(name = "crop_qty")
    private Integer cropQty;

    @Column(name = "crop_type")
    private String cropType;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;
}
