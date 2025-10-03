package com.cropdeal.dealer.controller;

import com.cropdeal.dealer.dto.CropPublicDTO;
import com.cropdeal.dealer.dto.DealerDTO;
import com.cropdeal.dealer.dto.DealerDTO2; // This DTO is used in getAllDealers
import com.cropdeal.dealer.entity.Dealer;
import com.cropdeal.dealer.service.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus for ResponseEntity
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Correct import for Collectors

@RestController
@RequestMapping("/api/dealer")
public class DealerController {

    @Autowired
    private DealerService dealerService;

    @GetMapping("/profile/{dealerId}")
    public Dealer getProfile(@PathVariable Long dealerId) {
        // Exception handling for Dealer not found is now in the service layer
        return dealerService.getProfile(dealerId);
    }

    @PutMapping("/profile/{dealerId}")
    public Dealer updateProfile(@PathVariable Long dealerId, @RequestBody DealerDTO dto) {
        // Exception handling for Dealer not found is now in the service layer
        return dealerService.updateProfile(dealerId, dto);
    }

    @GetMapping("/crops")
    public ResponseEntity<List<CropPublicDTO>> viewAllCrops() {
        List<CropPublicDTO> crops = dealerService.viewAllCropsWithFarmerInfo();
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/all")
    public List<DealerDTO2> getAllDealers() {
        List<Dealer> dealers = dealerService.getAllDealers();

        // Convert entity to DTO with role
        return dealers.stream().map(dealer -> {
            DealerDTO2 dto = new DealerDTO2();
            dto.setUserId(dealer.getId());
            dto.setName(dealer.getUser().getUsername());
            dto.setMobileNumber(dealer.getUser().getMobileNumber());
            dto.setAddress(dealer.getUser().getAddress());
            dto.setStatus(dealer.getUser().isActive());
            dto.setRole("DEALER");
            return dto;
        }).collect(Collectors.toList()); // Using .collect(Collectors.toList())
    }
 
    @PutMapping("/crops/reduce-quantity/{cropId}")
    public ResponseEntity<?> reduceCropQuantity(
            @PathVariable Long cropId,
            @RequestParam Integer quantity) { // Taking quantity to reduce as a request parameter
        try {
            CropPublicDTO updatedCrop = dealerService.reduceCropQuantity(cropId, quantity);
            if (updatedCrop == null) {
                // This means the crop's quantity dropped to 0 or less, and it was deleted
                return ResponseEntity.ok("Crop with ID " + cropId + " quantity reduced to zero or less and deleted successfully.");
            }
            return ResponseEntity.ok(updatedCrop); // Return the updated crop DTO
        } catch (IllegalArgumentException e) {
            // Catches errors like negative/zero quantity input or insufficient stock
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            // Catches RuntimeExceptions thrown by the service (e.g., crop not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/crops/{cropId}")
    public ResponseEntity<String> deleteCrop(@PathVariable Long cropId) {
        try {
            dealerService.deleteCrop(cropId);
            return ResponseEntity.ok("Crop with ID " + cropId + " deleted successfully.");
        } catch (RuntimeException e) {
            // Catches RuntimeException thrown by the service (e.g., crop not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}