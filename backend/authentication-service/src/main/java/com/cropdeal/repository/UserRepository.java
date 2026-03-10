package com.cropdeal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cropdeal.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findUserByUsername(String username);
	boolean existsByUsername(String username);

}
