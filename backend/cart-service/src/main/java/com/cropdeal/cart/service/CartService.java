package com.cropdeal.cart.service;

import com.cropdeal.cart.dto.CropInfoDTO;
import com.cropdeal.cart.entity.CartItem;
import com.cropdeal.cart.entity.Crop;
import com.cropdeal.cart.entity.Dealer;
import com.cropdeal.cart.entity.Farmer;
import com.cropdeal.cart.entity.User;
import com.cropdeal.cart.repository.CartItemRepository;
import com.cropdeal.cart.repository.CropRepository;
import com.cropdeal.cart.repository.DealerRepository;
import com.cropdeal.cart.repository.FarmerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final CropRepository cropRepo;
    private final DealerRepository dealerRepo;
    private final FarmerRepository farmerRepo;

    /**
     * Adds a crop to the dealer's cart.
     *
     * @param dealerId Dealer ID
     * @param cropId   Crop ID
     * @param quantity Quantity requested
     * @param farmerId Farmer ID
     * @return Saved CartItem
     */
    public CartItem addToCart(Long dealerId, Long cropId, Integer quantity, Integer farmerId) {
        // Check dealer existence
        boolean dealerExists = dealerRepo.existsById(dealerId.intValue());
        if (!dealerExists) {
            throw new IllegalArgumentException("Dealer not found with id: " + dealerId);
        }

        // Fetch crop
        Crop crop = cropRepo.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found with id: " + cropId));

        // Fetch Farmer using the provided farmerId
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId + "."));

        // Get farmer's username (with null checks)
        User farmerUser = farmer.getUser();
        String farmerUsername;
        if (farmerUser != null && farmerUser.getUsername() != null) {
            farmerUsername = farmerUser.getUsername();
        } else {
            farmerUsername = "Unknown Farmer";
            System.err.println("Warning: Farmer (ID: " + farmerId + ") has missing user or username. Defaulting to 'Unknown Farmer'.");
        }

        // Check quantity
        if (quantity > crop.getCropQty()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + crop.getCropQty() + " units).");
        }

        // Build CartItem
        CartItem item = CartItem.builder()
                .dealerId(dealerId)
                .cropId(crop.getId())
                .cropName(crop.getCropName())
                .imageUrl(crop.getImageUrl())
                .quantity(quantity)
                .price(crop.getCropPrice())
                .totalPrice(quantity * crop.getCropPrice())
                .farmerId(farmerId)
                .farmerName(farmerUsername)
                .build();

        return cartRepo.save(item);
    }

    /**
     * Retrieves all cart items for a given dealer.
     *
     * @param dealerId Dealer ID
     * @return List of CropInfoDTO
     */
    public List<CropInfoDTO> getCartItems(Long dealerId) {
        return cartRepo.findByDealerId(dealerId).stream()
                .map(item -> CropInfoDTO.builder()
                        .cartItemId(item.getId())
                        .cropId(item.getCropId())
                        .imageUrl(item.getImageUrl())
                        .quantity(item.getQuantity())
                        .cropPrice(item.getPrice())
                        .totalPrice(item.getTotalPrice())
                        .cropName(item.getCropName())
                        .farmerId(item.getFarmerId())
                        .farmerName(item.getFarmerName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Clears the cart of a specific dealer.
     *
     * @param dealerId Dealer ID
     */
    @Transactional
    public void clearCart(Long dealerId) {
        cartRepo.deleteByDealerId(dealerId);
    }
}
