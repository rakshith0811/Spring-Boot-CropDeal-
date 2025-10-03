// DealerRepository.java
package com.cropdeal.admin.repository;

import com.cropdeal.admin.entity.Dealer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends CrudRepository<Dealer, Integer> {
    @Query(value = "SELECT user_id FROM dealer WHERE id = :dealerId", nativeQuery = true)
    Integer findUserIdByDealerId(Integer dealerId);
}
