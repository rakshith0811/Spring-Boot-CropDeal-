package com.cropdeal.farmer.service;

import com.cropdeal.farmer.dto.CropPublicDTO;
import com.cropdeal.farmer.entity.Crop;
import com.cropdeal.farmer.entity.Farmer;
import com.cropdeal.farmer.repository.CropRepository;
import com.cropdeal.farmer.repository.FarmerRepository;
import com.cropdeal.farmer.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private FarmerRepository farmerRepo;

    @Autowired
    private CropRepository cropRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Override
    public Farmer updateProfile(Integer farmerId, String mobile, String address) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        farmer.getUser().setMobileNumber(mobile);
        farmer.getUser().setAddress(address);

        return farmerRepo.save(farmer);
    }

    @Override
    public Crop addCrop(Integer farmerId, Crop crop) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        crop.setFarmer(farmer);

        return cropRepo.save(crop);
    }

    @Override
    public List<Crop> getFarmerCrops(Integer farmerId) {
        return cropRepo.findByFarmerId(farmerId);
    }

    @Override
    public List<CropPublicDTO> getPublicCrops() {
        return cropRepo.findAll()
                .stream()
                .map(CropPublicDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Farmer getProfile(Integer farmerId) {
        return farmerRepo.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }
    @Override
    public List<Farmer> getAllFarmers() {
        return farmerRepo.findAll();
    }
    @Override
    public Crop updateCrop(Integer farmerId, Long cropId, Crop updatedCrop) {
        Crop existingCrop = cropRepo.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (!existingCrop.getFarmer().getId().equals(farmerId)) {
            throw new RuntimeException("Unauthorized to update this crop");
        }

        existingCrop.setCropName(updatedCrop.getCropName());
        existingCrop.setCropType(updatedCrop.getCropType());
        existingCrop.setCropQty(updatedCrop.getCropQty());
        existingCrop.setCropPrice(updatedCrop.getCropPrice());
        existingCrop.setCropDescription(updatedCrop.getCropDescription());
        existingCrop.setImageUrl(updatedCrop.getImageUrl());
        // Add any other fields you have in your Crop entity

        return cropRepo.save(existingCrop);
    }

    @Override
    public void deleteCrop(Integer farmerId, Long cropId) {
        Crop existingCrop = cropRepo.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (!existingCrop.getFarmer().getId().equals(farmerId)) {
            throw new RuntimeException("Unauthorized to delete this crop");
        }

        boolean isOrdered = orderRepo.existsByCrop_Id(cropId);
        if (isOrdered) {
            throw new RuntimeException("Your crop is ordered by a dealer and cannot be deleted.");
        }

        cropRepo.delete(existingCrop);
    }



}
