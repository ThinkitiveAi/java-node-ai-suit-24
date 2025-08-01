package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.ProviderRegistrationRequest;
import com.thinkitve.aidemo.dto.ProviderRegistrationResponse;
import com.thinkitve.aidemo.dto.ProviderUpdateRequest;
import com.thinkitve.aidemo.dto.ProviderListResponse;
import com.thinkitve.aidemo.entity.Provider;
import com.thinkitve.aidemo.entity.Provider.VerificationStatus;
import com.thinkitve.aidemo.entity.ClinicAddress;
import com.thinkitve.aidemo.repository.ProviderRepository;
import com.thinkitve.aidemo.service.ProviderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ProviderServiceImpl.class, BCryptPasswordEncoder.class})
public class ProviderServiceImplCRUDTest {
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ProviderServiceImpl providerService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Provider testProvider;

    @BeforeEach
    void setup() {
        ClinicAddress address = ClinicAddress.builder()
                .street("123 Main St")
                .city("Metropolis")
                .state("State")
                .zip("12345")
                .build();

        testProvider = Provider.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12345678901")
                .passwordHash(passwordEncoder.encode("StrongP@ssw0rd"))
                .specialization("Cardiology")
                .licenseNumber("LIC12345")
                .yearsOfExperience(10)
                .clinicAddress(address)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
        providerRepository.save(testProvider);
    }

    @Test
    void testGetAllProviders_Success() {
        ProviderListResponse response = providerService.getAllProviders(null, null, null, null, 0, 20);

        assertTrue(response.isSuccess());
        assertEquals("Providers retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(1, response.getData().size());
        assertEquals("John", response.getData().get(0).getFirstName());
    }

    @Test
    void testGetAllProviders_WithFilters() {
        ProviderListResponse response = providerService.getAllProviders("Cardiology", "Metropolis", "State", true, 0, 20);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("Cardiology", response.getData().get(0).getSpecialization());
    }

    @Test
    void testGetAllProviders_NoResults() {
        ProviderListResponse response = providerService.getAllProviders("Dermatology", null, null, null, 0, 20);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetProviderById_Success() {
        ProviderRegistrationResponse response = providerService.getProviderById(testProvider.getId());

        assertTrue(response.isSuccess());
        assertEquals("John", response.getFirstName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("Cardiology", response.getSpecialization());
    }

    @Test
    void testGetProviderById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> providerService.getProviderById(nonExistentId));
        assertEquals("Provider not found", ex.getMessage());
    }

    @Test
    void testUpdateProvider_Success() {
        ProviderUpdateRequest updateRequest = ProviderUpdateRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .specialization("Neurology")
                .yearsOfExperience(15)
                .clinicAddress(ProviderUpdateRequest.ClinicAddressDTO.builder()
                        .street("456 Oak St")
                        .city("New City")
                        .state("New State")
                        .zip("54321")
                        .build())
                .build();

        ProviderRegistrationResponse response = providerService.updateProvider(testProvider.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("Neurology", response.getSpecialization());
        assertEquals(15, response.getYearsOfExperience());
        assertEquals("456 Oak St", response.getClinicAddress().getStreet());
    }

    @Test
    void testUpdateProvider_PartialUpdate() {
        ProviderUpdateRequest updateRequest = ProviderUpdateRequest.builder()
                .firstName("Jane")
                .build();

        ProviderRegistrationResponse response = providerService.updateProvider(testProvider.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName()); // Should remain unchanged
        assertEquals("Cardiology", response.getSpecialization()); // Should remain unchanged
    }

    @Test
    void testUpdateProvider_DuplicatePhoneNumber() {
        // Create another provider with different phone
        ClinicAddress address = ClinicAddress.builder()
                .street("789 Pine St")
                .city("Other City")
                .state("Other State")
                .zip("67890")
                .build();

        Provider anotherProvider = Provider.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+12345678902")
                .passwordHash(passwordEncoder.encode("StrongP@ssw0rd"))
                .specialization("Dermatology")
                .licenseNumber("LIC67890")
                .yearsOfExperience(5)
                .clinicAddress(address)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
        providerRepository.save(anotherProvider);

        // Try to update first provider with second provider's phone number
        ProviderUpdateRequest updateRequest = ProviderUpdateRequest.builder()
                .phoneNumber("+12345678902")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> providerService.updateProvider(testProvider.getId(), updateRequest));
        assertEquals("Phone number already registered", ex.getMessage());
    }

    @Test
    void testUpdateProvider_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        ProviderUpdateRequest updateRequest = ProviderUpdateRequest.builder()
                .firstName("Jane")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> providerService.updateProvider(nonExistentId, updateRequest));
        assertEquals("Provider not found", ex.getMessage());
    }

    @Test
    void testDeleteProvider_Success() {
        ProviderRegistrationResponse response = providerService.deleteProvider(testProvider.getId());

        assertTrue(response.isSuccess());
        assertEquals("Provider deleted successfully", response.getMessage());

        // Verify soft delete - provider should still exist but be inactive
        assertTrue(providerRepository.findById(testProvider.getId()).isPresent());
        Provider deletedProvider = providerRepository.findById(testProvider.getId()).get();
        assertFalse(deletedProvider.isActive());
    }

    @Test
    void testDeleteProvider_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> providerService.deleteProvider(nonExistentId));
        assertEquals("Provider not found", ex.getMessage());
    }

    @Test
    void testDeleteProvider_AlreadyDeleted() {
        // First delete
        providerService.deleteProvider(testProvider.getId());

        // Try to delete again
        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> providerService.deleteProvider(testProvider.getId()));
        assertEquals("Provider not found", ex.getMessage());
    }

    @Test
    void testPaginationInfo() {
        // Create additional providers for pagination testing
        for (int i = 1; i <= 25; i++) {
            ClinicAddress address = ClinicAddress.builder()
                    .street(i + " Test St")
                    .city("Test City")
                    .state("Test State")
                    .zip("12345")
                    .build();

            Provider provider = Provider.builder()
                    .firstName("Provider" + i)
                    .lastName("Test")
                    .email("provider" + i + "@test.com")
                    .phoneNumber("+1234567890" + i)
                    .passwordHash(passwordEncoder.encode("StrongP@ssw0rd"))
                    .specialization("Test Specialty")
                    .licenseNumber("LIC" + i)
                    .yearsOfExperience(i)
                    .clinicAddress(address)
                    .verificationStatus(VerificationStatus.PENDING)
                    .isActive(true)
                    .build();
            providerRepository.save(provider);
        }

        ProviderListResponse response = providerService.getAllProviders(null, null, null, null, 0, 10);

        assertTrue(response.isSuccess());
        assertNotNull(response.getPagination());
        assertEquals(26, response.getPagination().getTotalElements()); // 25 new + 1 original
        assertEquals(3, response.getPagination().getTotalPages()); // 26 items with page size 10
        assertEquals(0, response.getPagination().getCurrentPage());
        assertEquals(10, response.getPagination().getPageSize());
        assertTrue(response.getPagination().isHasNext());
        assertFalse(response.getPagination().isHasPrevious());
    }
} 