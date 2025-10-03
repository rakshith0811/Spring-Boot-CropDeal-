package com.cropdeal.admin.client;

import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "farmer-service", url = "${farmer.service.url:http://localhost:8083}")
public interface FarmerAdminClient {
    @GetMapping("/api/farmer/all")
    List<FarmerDTO> getAllFarmers();

    @PutMapping("/api/farmer/status/{id}")
    FarmerDTO updateFarmerStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto);
}
