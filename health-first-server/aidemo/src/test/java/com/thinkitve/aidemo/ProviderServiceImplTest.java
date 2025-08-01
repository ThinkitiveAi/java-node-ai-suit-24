package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.ProviderRegistrationRequest;
import com.thinkitve.aidemo.entity.Provider;
import com.thinkitve.aidemo.repository.ProviderRepository;
import com.thinkitve.aidemo.service.ProviderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ProviderServiceImpl.class, BCryptPasswordEncoder.class})
public class ProviderServiceImplTest {
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ProviderServiceImpl providerService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private ProviderRegistrationRequest.ClinicAddressDTO addressDTO;

    @BeforeEach
    void setup() {
        addressDTO = ProviderRegistrationRequest.ClinicAddressDTO.builder()
                .street("123 Main St")
                .city("Metropolis")
                .state("State")
                .zip("12345")
                .build();
    }

    @Test
    void testRegisterProvider_Success() {
        ProviderRegistrationRequest req = ProviderRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12345678901")
                .password("StrongP@ssw0rd")
                .specialization("Cardiology")
                .licenseNumber("LIC12345")
                .yearsOfExperience(10)
                .clinicAddress(addressDTO)
                .build();
        var response = providerService.registerProvider(req);
        assertNotNull(response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("pending", response.getVerificationStatus());
        Provider saved = providerRepository.findById(response.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("StrongP@ssw0rd", saved.getPasswordHash()));
    }

    @Test
    void testRegisterProvider_DuplicateEmail() {
        ProviderRegistrationRequest req1 = ProviderRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+12345678902")
                .password("StrongP@ssw0rd1")
                .specialization("Dermatology")
                .licenseNumber("LIC54321")
                .clinicAddress(addressDTO)
                .build();
        providerService.registerProvider(req1);
        ProviderRegistrationRequest req2 = ProviderRegistrationRequest.builder()
                .firstName("Janet")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+12345678903")
                .password("StrongP@ssw0rd2")
                .specialization("Dermatology")
                .licenseNumber("LIC54322")
                .clinicAddress(addressDTO)
                .build();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> providerService.registerProvider(req2));
        assertTrue(ex.getMessage().contains("Email already registered"));
    }

    @Test
    void testRegisterProvider_DuplicatePhone() {
        ProviderRegistrationRequest req1 = ProviderRegistrationRequest.builder()
                .firstName("Alice")
                .lastName("Brown")
                .email("alice.brown@example.com")
                .phoneNumber("+12345678904")
                .password("StrongP@ssw0rd3")
                .specialization("Neurology")
                .licenseNumber("LIC67890")
                .clinicAddress(addressDTO)
                .build();
        providerService.registerProvider(req1);
        ProviderRegistrationRequest req2 = ProviderRegistrationRequest.builder()
                .firstName("Alicia")
                .lastName("Brown")
                .email("alicia.brown@example.com")
                .phoneNumber("+12345678904")
                .password("StrongP@ssw0rd4")
                .specialization("Neurology")
                .licenseNumber("LIC67891")
                .clinicAddress(addressDTO)
                .build();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> providerService.registerProvider(req2));
        assertTrue(ex.getMessage().contains("Phone number already registered"));
    }

    @Test
    void testRegisterProvider_WeakPassword() {
        ProviderRegistrationRequest req = ProviderRegistrationRequest.builder()
                .firstName("Bob")
                .lastName("White")
                .email("bob.white@example.com")
                .phoneNumber("+12345678905")
                .password("weakpass")
                .specialization("Pediatrics")
                .licenseNumber("LIC99999")
                .clinicAddress(addressDTO)
                .build();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> providerService.registerProvider(req));
        assertTrue(ex.getMessage().contains("Password does not meet strength requirements"));
    }

    @Test
    void testRegisterProvider_DuplicateLicense() {
        ProviderRegistrationRequest req1 = ProviderRegistrationRequest.builder()
                .firstName("Carl")
                .lastName("Green")
                .email("carl.green@example.com")
                .phoneNumber("+12345678906")
                .password("StrongP@ssw0rd5")
                .specialization("Oncology")
                .licenseNumber("LIC77777")
                .clinicAddress(addressDTO)
                .build();
        providerService.registerProvider(req1);
        ProviderRegistrationRequest req2 = ProviderRegistrationRequest.builder()
                .firstName("Carla")
                .lastName("Green")
                .email("carla.green@example.com")
                .phoneNumber("+12345678907")
                .password("StrongP@ssw0rd6")
                .specialization("Oncology")
                .licenseNumber("LIC77777")
                .clinicAddress(addressDTO)
                .build();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> providerService.registerProvider(req2));
        assertTrue(ex.getMessage().contains("License number already registered"));
    }
} 