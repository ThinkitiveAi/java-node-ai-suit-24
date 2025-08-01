package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.LoginRequest;
import com.thinkitve.aidemo.dto.LoginResponse;
import com.thinkitve.aidemo.entity.Provider;
import com.thinkitve.aidemo.entity.Provider.VerificationStatus;
import com.thinkitve.aidemo.entity.ClinicAddress;
import com.thinkitve.aidemo.repository.ProviderRepository;
import com.thinkitve.aidemo.service.AuthServiceImpl;
import com.thinkitve.aidemo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AuthServiceImpl.class, BCryptPasswordEncoder.class, JwtUtil.class})
public class AuthServiceImplTest {
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

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
    void testLogin_Success() {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("StrongP@ssw0rd")
                .build();

        LoginResponse response = authService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Bearer", response.getData().getToken_type());
        assertEquals(3600, response.getData().getExpires_in());
        assertNotNull(response.getData().getAccess_token());
        assertEquals("John", response.getData().getProvider().getFirstName());
        assertEquals("Cardiology", response.getData().getProvider().getSpecialization());
    }

    @Test
    void testLogin_InvalidEmail() {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("StrongP@ssw0rd")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("WrongPassword")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void testLogin_DeactivatedAccount() {
        // Deactivate the provider
        testProvider.setActive(false);
        providerRepository.save(testProvider);

        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("StrongP@ssw0rd")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertEquals("Account is deactivated", ex.getMessage());
    }

    @Test
    void testJwtToken_GenerationAndValidation() {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("StrongP@ssw0rd")
                .build();

        LoginResponse response = authService.login(request);
        String token = response.getData().getAccess_token();

        // Validate token
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));

        // Extract claims
        assertEquals("john.doe@example.com", jwtUtil.extractEmail(token));
        assertEquals(testProvider.getId(), jwtUtil.extractProviderId(token));
    }
} 