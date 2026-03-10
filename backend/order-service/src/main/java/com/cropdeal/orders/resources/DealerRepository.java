package com.cropdeal.orders.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cropdeal.orders.models.Dealer;

public interface DealerRepository extends JpaRepository<Dealer, Long> {
}