package com.cropdeal.orders.resources;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cropdeal.orders.models.Orders;

public interface OrdersRepository extends JpaRepository<Orders, String> {
    // Find orders by farmer ID. Note: Ensure consistency of ID type (Integer vs Long) across services.
    List<Orders> findByFarmerId(Integer farmerId); // Original method, assuming Integer
    List<Orders> findByFarmerId(Long farmerId);   // Added/kept for Long type consistency if needed

    // Find orders by dealer ID. Note: Ensure consistency of ID type (Integer vs Long) across services.
    List<Orders> findByDealerId(Integer dealerId); // Original method, assuming Integer
    List<Orders> findByDealerId(Long dealerId);   // Added/kept for Long type consistency if needed

    // Find order by its unique String orderID
    Optional<Orders> findByOrderID(String orderID);
}
