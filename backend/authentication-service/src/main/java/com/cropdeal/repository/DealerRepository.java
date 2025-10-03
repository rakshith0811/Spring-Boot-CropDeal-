package com.cropdeal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.model.Dealer;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Integer> {
	Dealer findByUser_Username(String username);
}
