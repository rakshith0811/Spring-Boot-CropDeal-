package com.cropdeal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.model.Farmer;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Integer> {
	Farmer findByUser_Username(String username);
	
}

