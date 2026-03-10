package com.cropdeal.admin.client;

import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "dealer-service", url = "${dealer.service.url:http://localhost:8082}")
public interface DealerAdminClient {
    @GetMapping("/api/dealer/all")
    List<DealerDTO> getAllDealers();

    @PutMapping("/api/dealer/status/{id}")
    DealerDTO updateDealerStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto);
}
