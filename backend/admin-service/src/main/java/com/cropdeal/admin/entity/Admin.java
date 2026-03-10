package com.cropdeal.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

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

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian number")
    private String mobileNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String address;

    private boolean active;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|FARMER|DEALER", message = "Role must be one of: ADMIN, FARMER, DEALER")
    private String role;
}
