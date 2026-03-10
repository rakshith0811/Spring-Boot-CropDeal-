package com.cropdeal.cart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private Integer id;
    private String username;
    private Boolean active;
    private String address;
    private String mobileNumber;
    private String password;
    private String role;
}
