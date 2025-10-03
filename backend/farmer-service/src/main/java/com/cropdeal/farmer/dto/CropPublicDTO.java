package com.cropdeal.farmer.dto;

import com.cropdeal.farmer.entity.Crop;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CropPublicDTO {

    private String cropName;
    private String cropType;
    private Integer cropQty;
    private Double cropPrice;
    private String cropDescription;
    private String imageUrl;

    private String farmerName;    // from User.username
    private String farmerMobile;  // from User.mobileNumber
    private String farmerAddress; // from User.address

    public CropPublicDTO(Crop crop) {
        this.cropName = crop.getCropName();
        this.cropType = crop.getCropType();
        this.cropQty = crop.getCropQty();
        this.cropPrice = crop.getCropPrice();
        this.cropDescription = crop.getCropDescription();
        this.imageUrl = crop.getImageUrl();

        this.farmerName = crop.getFarmer().getUser().getUsername();
        this.farmerMobile = crop.getFarmer().getUser().getMobileNumber();
        this.farmerAddress = crop.getFarmer().getUser().getAddress();
    }
}
