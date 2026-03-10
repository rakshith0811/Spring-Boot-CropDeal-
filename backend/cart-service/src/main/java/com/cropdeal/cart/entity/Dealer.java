package com.cropdeal.cart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dealer")
@Data
public class Dealer {
    @Id
    private Integer id;

    @Column(name = "user_id", unique = true)
    private Integer userId;
}
