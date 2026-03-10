package com.cropdeal.payment_service.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
	@NotNull(message="amount should not be null")
    private Long amount;
	@NotNull(message="quantity should not be null")
	@Min(value=1)
    private Long quantity;
	@NotBlank(message="name should not be blank")
    private String name;
	@NotBlank(message="currency should not be blank")
    private String currency;
    
	
}