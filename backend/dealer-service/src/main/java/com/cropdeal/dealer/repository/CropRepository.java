package com.cropdeal.dealer.repository;

import com.cropdeal.dealer.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Import this
import org.springframework.data.jpa.repository.Query; // Import this
import org.springframework.transaction.annotation.Transactional; // Import this (Spring's @Transactional)

import java.util.List;
import java.util.Optional; // Import Optional

public interface CropRepository extends JpaRepository<Crop, Long> {

	@Query("SELECT c FROM Crop c JOIN FETCH c.farmer f JOIN FETCH f.user WHERE f.user.active = true") // <--- ADDED WHERE CLAUSE
    List<Crop> findAllWithFarmerAndUser();

    // The other methods should also consider this if they expose crops publicly
    @Query("SELECT c FROM Crop c JOIN FETCH c.farmer f JOIN FETCH f.user WHERE c.id = :cropId AND f.user.active = true") // <--- ADDED WHERE CLAUSE
    Optional<Crop> findByIdWithFarmerAndUser(Long cropId);

    // --- NEW METHOD TO UPDATE CROP QUANTITY DIRECTLY ---
    @Modifying // Indicates that this query modifies data
    @Transactional // Ensures the operation is atomic at the repository level
    @Query("UPDATE Crop c SET c.cropQty = c.cropQty - :quantityReduced WHERE c.id = :cropId")
    int reduceCropQuantity(Long cropId, Integer quantityReduced);
}