package com.cropdeal.admin.controller;

import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;
import com.cropdeal.admin.entity.Admin;
import com.cropdeal.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired 
    private AdminService service;

    // Get admin profile
    @GetMapping("/profile/{id}")
    public Admin getProfile(@PathVariable Long id) {
        return service.getProfile(id);
    }

    // Update admin profile
    @PutMapping("/profile/{id}")
    public Admin updateProfile(
            @PathVariable Long id,
            @RequestBody Admin admin) {
        return service.updateProfile(id, admin);
    }

    // List all admins
    @GetMapping("/admins")
    public List<Admin> allAdmins() {
        return service.listAdmins();
    }

    // Manage farmers
    @GetMapping("/farmers")
    public List<FarmerDTO> allFarmers() {
        return service.listFarmers();
    }

    

    // Manage dealers
    @GetMapping("/dealers")
    public List<DealerDTO> allDealers() {
        return service.listDealers();
    }

    
    @PutMapping("/farmers/status/{farmerId}")
    public Admin changeFarmerStatus(@PathVariable Integer farmerId, @RequestBody StatusUpdateDTO dto) {
        return service.setFarmerStatus(farmerId, dto);
    }

    @PutMapping("/dealers/status/{dealerId}")
    public Admin changeDealerStatus(@PathVariable Integer dealerId, @RequestBody StatusUpdateDTO dto) {
        return service.setDealerStatus(dealerId, dto);
    }

}
