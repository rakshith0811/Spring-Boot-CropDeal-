package com.cropdeal.dealer.dto;

import com.cropdeal.dealer.entity.Crop;
import com.cropdeal.dealer.entity.Farmer;
import lombok.*;

@Data
@NoArgsConstructor
public class CropPublicDTO {
	private Long id;
    private String cropName;
    private String cropType;
    private Integer cropQty;
    private Double cropPrice;
    private String cropDescription;
    private String imageUrl;
    private Long farmerId;
    private String farmerName;
    private String farmerMobile;
    private String farmerAddress;

    public CropPublicDTO(Crop crop) {
    	this.id=crop.getId();
        this.cropName = crop.getCropName();
        this.cropType = crop.getCropType();
        this.cropQty = crop.getCropQty();
        this.cropPrice = crop.getCropPrice();
        this.cropDescription = crop.getCropDescription();
        this.imageUrl = crop.getImageUrl();

        Farmer f = crop.getFarmer();
        if (f != null && f.getUser() != null) {
        	this.farmerId=f.getId();
            this.farmerName = f.getUser().getUsername();
            this.farmerMobile = f.getUser().getMobileNumber();
            this.farmerAddress = f.getUser().getAddress();
        }
    }
}
