package com.cropdeal.cart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "farmer")
@Data
public class Farmer {
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
