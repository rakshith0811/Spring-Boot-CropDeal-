package com.cropdeal.admin.dto;

import lombok.Data;

@Data
public class DealerDTO {
    private Long userId;
    private String name;
    private String mobileNumber;
    private String address;
    private boolean status;   // active/inactive
    private String role;      // e.g., "DEALER"
}
