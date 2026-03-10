package com.cropdeal.farmer.dto;
import lombok.Data;

@Data
public class FarmerDTO {
    private long userId;
    private String name;
    private String mobileNumber;
    private String address;
    private boolean status;   // active/inactive
    private String role;      // e.g., "FARMER"
}
