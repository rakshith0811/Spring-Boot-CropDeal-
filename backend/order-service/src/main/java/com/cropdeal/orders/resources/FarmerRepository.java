package com.cropdeal.orders.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cropdeal.orders.models.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
}
