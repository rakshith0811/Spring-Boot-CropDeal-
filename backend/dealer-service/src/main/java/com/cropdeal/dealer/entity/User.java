package com.cropdeal.dealer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;  // Same id as in authentication service users table

    private String username;
    private String password;
    private String role;
    private String mobileNumber;
    private String address;
    private boolean active;
}
