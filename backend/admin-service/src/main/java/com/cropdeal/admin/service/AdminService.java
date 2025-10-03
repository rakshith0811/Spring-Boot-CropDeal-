package com.cropdeal.admin.service;

import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;
import com.cropdeal.admin.entity.Admin;

import java.util.List;

public interface AdminService {
    Admin getProfile(Long id);
    Admin updateProfile(Long id, Admin updated);

    List<Admin> listAdmins();          // NEW: List all admins

    List<FarmerDTO> listFarmers();
    

    List<DealerDTO> listDealers();
   
    Admin setFarmerStatus(Integer id, StatusUpdateDTO dto);

    Admin setDealerStatus(Integer id, StatusUpdateDTO dto);

}
