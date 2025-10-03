package com.cropdeal.admin.service;

import com.cropdeal.admin.client.DealerAdminClient;
import com.cropdeal.admin.client.FarmerAdminClient;
import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;
import com.cropdeal.admin.entity.Admin;
import com.cropdeal.admin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private AdminRepository repo;
    @Autowired private FarmerAdminClient farmerClient;
    @Autowired private DealerAdminClient dealerClient;
    @Autowired
    private FarmerRepository farmerRepo;

    @Autowired
    private DealerRepository dealerRepo;

    @Override
    public Admin getProfile(Long id) {
        Admin admin = repo.findAdminById(id);
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        return admin;
    }

    @Override
    public Admin updateProfile(Long id, Admin updated) {
        Admin a = getProfile(id);
        a.setUsername(updated.getUsername());
        a.setMobileNumber(updated.getMobileNumber());
        a.setAddress(updated.getAddress());
        a.setActive(updated.isActive());
        // do not allow role or password update here for security
        return repo.save(a);
    }

    @Override
    public List<Admin> listAdmins() {
        return repo.findAllAdmins();
    }

    @Override
    public List<FarmerDTO> listFarmers() {
        return farmerClient.getAllFarmers();
    }

    @Override
    public Admin setFarmerStatus(Integer farmerId, StatusUpdateDTO dto) {
        Integer userId = farmerRepo.findUserIdByFarmerId(farmerId);
        if (userId == null) throw new RuntimeException("Invalid Farmer ID");

        int updated = repo.updateActiveStatusByRole(userId, dto.isActive(), "FARMER");
        if (updated == 1) {
            return repo.findById(userId).orElse(null);
        }
        return null;
    }

    @Override
    public Admin setDealerStatus(Integer dealerId, StatusUpdateDTO dto) {
        Integer userId = dealerRepo.findUserIdByDealerId(dealerId);
        if (userId == null) throw new RuntimeException("Invalid Dealer ID");

        int updated = repo.updateActiveStatusByRole(userId, dto.isActive(), "DEALER");
        if (updated == 1) {
            return repo.findById(userId).orElse(null);
        }
        return null;
    }

    @Override
    public List<DealerDTO> listDealers() {
        return dealerClient.getAllDealers();
    }
}
