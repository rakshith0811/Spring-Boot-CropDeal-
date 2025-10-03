package com.cropdeal.admin.controller;

import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;
import com.cropdeal.admin.entity.Admin;
import com.cropdeal.admin.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService service;

    @InjectMocks
    private AdminController controller;

    @Test
    void testGetProfile() {
        Admin admin = new Admin(1, "admin1", "pass", "9999999999", "address", true, "ADMIN");
        when(service.getProfile(1L)).thenReturn(admin);

        Admin result = controller.getProfile(1L);

        assertEquals(admin, result);
    }

    @Test
    void testUpdateProfile() {
        Admin input = new Admin(1, "updated", "pass", "9999999999", "address", true, "ADMIN");
        when(service.updateProfile(1L, input)).thenReturn(input);

        Admin result = controller.updateProfile(1L, input);

        assertEquals(input, result);
    }

    @Test
    void testAllAdmins() {
        List<Admin> admins = Arrays.asList(new Admin(), new Admin());
        when(service.listAdmins()).thenReturn(admins);

        List<Admin> result = controller.allAdmins();

        assertEquals(2, result.size());
    }

    @Test
    void testAllFarmers() {
        List<FarmerDTO> farmers = Arrays.asList(new FarmerDTO(), new FarmerDTO());
        when(service.listFarmers()).thenReturn(farmers);

        List<FarmerDTO> result = controller.allFarmers();

        assertEquals(2, result.size());
    }

    @Test
    void testAllDealers() {
        List<DealerDTO> dealers = Arrays.asList(new DealerDTO(), new DealerDTO());
        when(service.listDealers()).thenReturn(dealers);

        List<DealerDTO> result = controller.allDealers();

        assertEquals(2, result.size());
    }

    @Test
    void testChangeFarmerStatus() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setActive(false);
        Admin admin = new Admin();
        when(service.setFarmerStatus(1, dto)).thenReturn(admin);

        Admin result = controller.changeFarmerStatus(1, dto);

        assertEquals(admin, result);
    }

    @Test
    void testChangeDealerStatus() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setActive(true);
        Admin admin = new Admin();
        when(service.setDealerStatus(2, dto)).thenReturn(admin);

        Admin result = controller.changeDealerStatus(2, dto);

        assertEquals(admin, result);
    }
}
