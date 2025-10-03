package com.cropdeal.cart.repository;

import com.cropdeal.cart.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DealerRepository extends JpaRepository<Dealer, Integer> {
}
