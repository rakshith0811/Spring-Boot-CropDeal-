package com.cropdeal.orders.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cropdeal.orders.models.Crop;

public interface CropRepository extends JpaRepository<Crop, Long> {
}