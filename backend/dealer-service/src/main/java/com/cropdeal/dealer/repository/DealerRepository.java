package com.cropdeal.dealer.repository;

import com.cropdeal.dealer.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Optional<Dealer> findByUser_Id(Long userId); // fixed method name
}
