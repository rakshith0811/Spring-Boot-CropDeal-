package com.cropdeal.admin.service;

import com.cropdeal.admin.client.DealerAdminClient;
import com.cropdeal.admin.client.FarmerAdminClient;
import com.cropdeal.admin.dto.DealerDTO;
import com.cropdeal.admin.dto.FarmerDTO;
import com.cropdeal.admin.dto.StatusUpdateDTO;
import com.cropdeal.admin.entity.Admin;
import com.cropdeal.admin.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock private AdminRepository adminRepo;
    @Mock private FarmerAdminClient farmerClient;
    @Mock private DealerAdminClient dealerClient;
    @Mock private FarmerRepository farmerRepo;
    @Mock private DealerRepository dealerRepo;

    @InjectMocks
    private AdminServiceImpl service;

    @Test
    void testGetProfile_Success() {
        Admin admin = new Admin();
        when(adminRepo.findAdminById(1L)).thenReturn(admin);

        Admin result = service.getProfile(1L);

        assertEquals(admin, result);
    }

    @Test
    void testGetProfile_NotFound() {
        when(adminRepo.findAdminById(2L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.getProfile(2L));
    }

    @Test
    void testUpdateProfile() {
        Admin existing = new Admin(1, "old", "pass", "999", "old", true, "ADMIN");
        Admin updated = new Admin(1, "new", "pass", "888", "new", false, "ADMIN");

        when(adminRepo.findAdminById(1L)).thenReturn(existing);
        when(adminRepo.save(any(Admin.class))).thenReturn(updated);

        Admin result = service.updateProfile(1L, updated);

        assertEquals("new", result.getUsername());
        assertEquals("888", result.getMobileNumber());
    }

    @Test
    void testListAdmins() {
        List<Admin> admins = List.of(new Admin(), new Admin());
        when(adminRepo.findAllAdmins()).thenReturn(admins);

        List<Admin> result = service.listAdmins();

        assertEquals(2, result.size());
    }

    @Test
    void testListFarmers() {
        List<FarmerDTO> farmers = List.of(new FarmerDTO());
        when(farmerClient.getAllFarmers()).thenReturn(farmers);

        assertEquals(1, service.listFarmers().size());
    }

    @Test
    void testListDealers() {
        List<DealerDTO> dealers = List.of(new DealerDTO());
        when(dealerClient.getAllDealers()).thenReturn(dealers);

        assertEquals(1, service.listDealers().size());
    }

    @Test
    void testSetFarmerStatus() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setActive(true);

        when(farmerRepo.findUserIdByFarmerId(1)).thenReturn(10);
        when(adminRepo.updateActiveStatusByRole(10, true, "FARMER")).thenReturn(1);
        when(adminRepo.findById(10)).thenReturn(Optional.of(new Admin()));

        assertNotNull(service.setFarmerStatus(1, dto));
    }

    @Test
    void testSetDealerStatus() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setActive(false);

        when(dealerRepo.findUserIdByDealerId(2)).thenReturn(11);
        when(adminRepo.updateActiveStatusByRole(11, false, "DEALER")).thenReturn(1);
        when(adminRepo.findById(11)).thenReturn(Optional.of(new Admin()));

        assertNotNull(service.setDealerStatus(2, dto));
    }
}
