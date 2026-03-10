package com.cropdeal.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cropdeal.admin.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Query("SELECT a FROM Admin a WHERE a.role = 'ADMIN'")
    List<Admin> findAllAdmins();

    @Query("SELECT a FROM Admin a WHERE a.id = :id AND a.role = 'ADMIN'")
    Admin findAdminById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Admin a SET a.active = :active WHERE a.id = :id AND LOWER(a.role) = LOWER(:role)")
    int updateActiveStatusByRole(@Param("id") Integer id,
                                 @Param("active") boolean active,
                                 @Param("role") String role);

}
