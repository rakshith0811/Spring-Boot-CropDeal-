package com.cropdeal.dealer.service;

import com.cropdeal.dealer.dto.CropPublicDTO;
import com.cropdeal.dealer.dto.DealerDTO; // Used for updateProfile method
import com.cropdeal.dealer.entity.Dealer;

import java.util.List;

public interface DealerService {
    Dealer getProfile(Long id);
    Dealer updateProfile(Long id, DealerDTO dto);
    List<CropPublicDTO> viewAllCropsWithFarmerInfo();
    List<Dealer> getAllDealers();

    // --- NEW METHODS FOR CROP QUANTITY UPDATE AND DELETE ---
    CropPublicDTO reduceCropQuantity(Long cropId, Integer quantityToReduce);
    void deleteCrop(Long cropId);
}