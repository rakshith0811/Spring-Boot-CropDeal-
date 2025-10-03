package com.cropdeal.farmer.service;

import com.cropdeal.farmer.dto.CropPublicDTO;
import com.cropdeal.farmer.entity.Crop;
import com.cropdeal.farmer.entity.Farmer;
import com.cropdeal.farmer.entity.User;
import com.cropdeal.farmer.repository.CropRepository;
import com.cropdeal.farmer.repository.FarmerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FarmerServiceImplTest {

    @Mock
    private FarmerRepository farmerRepo;

    @Mock
    private CropRepository cropRepo;

    @InjectMocks
    private FarmerServiceImpl farmerService;

    private Farmer farmer;
    private User user;
    private Crop crop;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, true, "Address1", "1234567890", "pass", "FARMER", "farmer1");
        farmer = new Farmer(1, user);
        crop = new Crop(1L, "Tomato", "vegetable", 100, 20.0, "Fresh tomatoes", "image.jpg", farmer);
    }

    @Test
    void updateProfile_Success() {
        when(farmerRepo.findById(1)).thenReturn(Optional.of(farmer));
        when(farmerRepo.save(any(Farmer.class))).thenAnswer(i -> i.getArgument(0));

        Farmer updated = farmerService.updateProfile(1, "0987654321", "New Address");

        assertThat(updated.getUser().getMobileNumber()).isEqualTo("0987654321");
        assertThat(updated.getUser().getAddress()).isEqualTo("New Address");
        verify(farmerRepo).save(farmer);
    }

    @Test
    void updateProfile_FarmerNotFound_Throws() {
        when(farmerRepo.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmerService.updateProfile(1, "mobile", "address"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Farmer not found");
    }

    @Test
    void addCrop_Success() {
        when(farmerRepo.findById(1)).thenReturn(Optional.of(farmer));
        when(cropRepo.save(any(Crop.class))).thenAnswer(i -> i.getArgument(0));

        Crop toAdd = new Crop(null, "Apple", "fruit", 50, 30.0, "Sweet apples", "img.png", null);

        Crop saved = farmerService.addCrop(1, toAdd);

        assertThat(saved.getFarmer()).isEqualTo(farmer);
        assertThat(saved.getCropName()).isEqualTo("Apple");
        verify(cropRepo).save(toAdd);
    }

    @Test
    void addCrop_FarmerNotFound_Throws() {
        when(farmerRepo.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmerService.addCrop(1, crop))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Farmer not found");
    }

    @Test
    void getFarmerCrops_ReturnsList() {
        when(cropRepo.findByFarmerId(1)).thenReturn(List.of(crop));

        List<Crop> crops = farmerService.getFarmerCrops(1);

        assertThat(crops).containsExactly(crop);
    }

    @Test
    void getPublicCrops_ReturnsDTOs() {
        when(cropRepo.findAll()).thenReturn(List.of(crop));

        List<CropPublicDTO> dtos = farmerService.getPublicCrops();

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getCropName()).isEqualTo("Tomato");
        assertThat(dtos.get(0).getFarmerName()).isEqualTo(user.getUsername());
    }

    @Test
    void getProfile_Success() {
        when(farmerRepo.findById(1)).thenReturn(Optional.of(farmer));
        Farmer found = farmerService.getProfile(1);
        assertThat(found).isEqualTo(farmer);
    }

    @Test
    void getProfile_NotFound_Throws() {
        when(farmerRepo.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmerService.getProfile(1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Farmer not found");
    }

    @Test
    void getAllFarmers_ReturnsList() {
        when(farmerRepo.findAll()).thenReturn(List.of(farmer));
        List<Farmer> farmers = farmerService.getAllFarmers();
        assertThat(farmers).containsExactly(farmer);
    }

    @Test
    void updateCrop_Success() {
        Crop updatedCrop = new Crop(null, "Potato", "vegetable", 200, 10.0, "Good potatoes", "img2.jpg", null);

        when(cropRepo.findById(1L)).thenReturn(Optional.of(crop));
        when(cropRepo.save(any(Crop.class))).thenAnswer(i -> i.getArgument(0));

        Crop result = farmerService.updateCrop(1, 1L, updatedCrop);

        assertThat(result.getCropName()).isEqualTo("Potato");
        assertThat(result.getCropQty()).isEqualTo(200);
    }

    @Test
    void updateCrop_Unauthorized_Throws() {
        Crop updatedCrop = new Crop();
        when(cropRepo.findById(1L)).thenReturn(Optional.of(crop));
        // change farmer id on crop to something else to simulate unauthorized
        crop.getFarmer().setId(2);

        assertThatThrownBy(() -> farmerService.updateCrop(1, 1L, updatedCrop))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unauthorized");
    }

    @Test
    void updateCrop_NotFound_Throws() {
        when(cropRepo.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> farmerService.updateCrop(1, 1L, crop))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Crop not found");
    }

    @Test
    void deleteCrop_Success() {
        when(cropRepo.findById(1L)).thenReturn(Optional.of(crop));
        doNothing().when(cropRepo).delete(crop);

        farmerService.deleteCrop(1, 1L);

        verify(cropRepo).delete(crop);
    }

    @Test
    void deleteCrop_Unauthorized_Throws() {
        crop.getFarmer().setId(2);
        when(cropRepo.findById(1L)).thenReturn(Optional.of(crop));

        assertThatThrownBy(() -> farmerService.deleteCrop(1, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unauthorized");
    }

    @Test
    void deleteCrop_NotFound_Throws() {
        when(cropRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> farmerService.deleteCrop(1, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Crop not found");
    }
}
