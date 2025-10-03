package com.cropdeal.cart.repository;

import com.cropdeal.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByDealerId(Long dealerId);
    void deleteByDealerId(Long dealerId);
}
