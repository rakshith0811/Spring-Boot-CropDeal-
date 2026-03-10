package com.cropdeal.farmer.repository;

import com.cropdeal.farmer.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    // Checks if any order exists with the given crop ID
	 boolean existsByCrop_Id(Long cropId);
}
