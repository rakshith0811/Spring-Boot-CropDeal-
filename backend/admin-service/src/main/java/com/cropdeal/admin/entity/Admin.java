package com.cropdeal.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")  // shared users table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    private Integer id;  
    private String username;
    private String password;
    private String mobileNumber;
    private String address;
    private boolean active;
    private String role;  // ADMIN, FARMER, DEALER
}
