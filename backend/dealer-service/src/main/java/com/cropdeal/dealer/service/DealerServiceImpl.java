package com.cropdeal.dealer.service;

import com.cropdeal.dealer.dto.CropPublicDTO;
import com.cropdeal.dealer.dto.DealerDTO; // Used for updateProfile method
import com.cropdeal.dealer.entity.Crop;
import com.cropdeal.dealer.entity.Dealer;
import com.cropdeal.dealer.entity.User; // Assuming User entity path is correct
import com.cropdeal.dealer.repository.CropRepository;
import com.cropdeal.dealer.repository.DealerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional; // Using jakarta.transaction.Transactional for service layer
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Import Optional
import java.util.stream.Collectors;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private CropRepository cropRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Dealer getProfile(Long dealerId) {
        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found with id: " + dealerId));
    }

    @Override
    @Transactional
    public Dealer updateProfile(Long dealerId, DealerDTO dto) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found with id: " + dealerId));

        User user = dealer.getUser();
        // This logic is directly from your original provide code for updateProfile
        user.setMobileNumber(dto.getMobileNumber());
        user.setAddress(dto.getAddress());

        entityManager.merge(user); // Merge the user changes

        return dealer; // Return the managed dealer entity
    }

    @Override
    public List<CropPublicDTO> viewAllCropsWithFarmerInfo() {
        List<Crop> crops = cropRepository.findAllWithFarmerAndUser();
        return crops.stream()
                .map(CropPublicDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dealer> getAllDealers() {
        return dealerRepository.findAll();
    }

    // --- NEW METHOD TO REDUCE CROP QUANTITY ---
    @Override
    @Transactional // Crucial for atomic update and potential delete
    public CropPublicDTO reduceCropQuantity(Long cropId, Integer quantityToReduce) {
        if (quantityToReduce == null || quantityToReduce <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be a positive integer.");
        }

        Optional<Crop> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) {
            throw new RuntimeException("Crop not found with ID: " + cropId);
        }

        Crop crop = optionalCrop.get();

        // Check if current quantity is sufficient before attempting update
        if (crop.getCropQty() < quantityToReduce) {
            throw new IllegalArgumentException("Cannot reduce quantity by " + quantityToReduce +
                                               ". Available quantity is only " + crop.getCropQty());
        }

        // Use the direct update query for efficiency and to minimize race conditions
        int updatedRows = cropRepository.reduceCropQuantity(cropId, quantityToReduce);

        if (updatedRows == 0) {
            // This case should be rare if findById succeeded, but provides robustness.
            throw new RuntimeException("Failed to update crop quantity for ID: " + cropId + ". Crop might have been removed concurrently.");
        }

        // Re-fetch the crop to get the updated quantity from the database
        // This is important because the direct SQL update bypasses Hibernate's first-level cache
        Crop updatedCrop = cropRepository.findByIdWithFarmerAndUser(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found after update with ID: " + cropId));


        if (updatedCrop.getCropQty() <= 0) {
            // If quantity is 0 or less, delete the crop
            cropRepository.delete(updatedCrop);
            // After deletion, return null to indicate to the controller that it was deleted
            return null;
        }

        return new CropPublicDTO(updatedCrop); // Return the DTO of the updated crop
    }

    // --- NEW METHOD TO DELETE A CROP ---
    @Override
    @Transactional // Ensure delete operation is transactional
    public void deleteCrop(Long cropId) {
        if (!cropRepository.existsById(cropId)) {
            throw new RuntimeException("Crop not found with ID: " + cropId);
        }
        cropRepository.deleteById(cropId);
    }
}