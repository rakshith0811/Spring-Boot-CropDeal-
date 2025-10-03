// FarmerRepository.java
package com.cropdeal.admin.repository;

import com.cropdeal.admin.entity.Farmer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends CrudRepository<Farmer, Integer> {
    @Query(value = "SELECT user_id FROM farmer WHERE id = :farmerId", nativeQuery = true)
    Integer findUserIdByFarmerId(Integer farmerId);
}
