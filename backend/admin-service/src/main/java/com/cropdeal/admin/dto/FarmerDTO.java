package com.cropdeal.admin.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class FarmerDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian number")
    private String mobileNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String address;

    private boolean status;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "FARMER", message = "Role must be FARMER")
    private String role;
}
