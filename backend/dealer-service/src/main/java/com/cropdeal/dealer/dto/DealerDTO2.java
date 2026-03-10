package com.cropdeal.dealer.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DealerDTO2 {
	private Long userId;
    private String name;
    private String mobileNumber;
    private String address;
    private boolean status;   // active/inactive
    private String role;
}
