package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.PatientLoginRequest;
import com.thinkitve.aidemo.dto.PatientLoginResponse;
import com.thinkitve.aidemo.entity.Patient;
import com.thinkitve.aidemo.entity.Address;
import com.thinkitve.aidemo.entity.EmergencyContact;
import com.thinkitve.aidemo.entity.InsuranceInfo;
import com.thinkitve.aidemo.repository.PatientRepository;
import com.thinkitve.aidemo.service.PatientAuthServiceImpl;
import com.thinkitve.aidemo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PatientAuthServiceImpl.class, BCryptPasswordEncoder.class, JwtUtil.class})
public class PatientAuthServiceImplTest {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientAuthServiceImpl patientAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    private Patient testPatient;

    @BeforeEach
    void setup() {
        Address address = Address.builder()
                .street("456 Main Street")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .build();

        EmergencyContact emergencyContact = EmergencyContact.builder()
                .name("John Smith")
                .phone("+1234567891")
                .relationship("spouse")
                .build();

        InsuranceInfo insuranceInfo = InsuranceInfo.builder()
                .provider("Blue Cross")
                .policyNumber("BC123456789")
                .build();

        testPatient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .passwordHash(passwordEncoder.encode("SecurePassword123!"))
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address(address)
                .emergencyContact(emergencyContact)
                .insuranceInfo(insuranceInfo)
                .medicalHistory(Arrays.asList("Hypertension", "Diabetes"))
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();
        patientRepository.save(testPatient);
    }

    @Test
    void testLogin_SuccessWithEmail() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("jane.smith@email.com")
                .password("SecurePassword123!")
                .build();

        PatientLoginResponse response = patientAuthService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Bearer", response.getData().getToken_type());
        assertEquals(1800, response.getData().getExpires_in());
        assertNotNull(response.getData().getAccess_token());
        assertEquals("Jane", response.getData().getPatient().getFirstName());
        assertEquals("jane.smith@email.com", response.getData().getPatient().getEmail());
    }

    @Test
    void testLogin_SuccessWithPhone() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("+1234567890")
                .password("SecurePassword123!")
                .build();

        PatientLoginResponse response = patientAuthService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData().getAccess_token());
        assertEquals("Jane", response.getData().getPatient().getFirstName());
    }

    @Test
    void testLogin_InvalidEmail() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("nonexistent@email.com")
                .password("SecurePassword123!")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientAuthService.login(request));
        assertEquals("Invalid email/phone or password", ex.getMessage());
    }

    @Test
    void testLogin_InvalidPhone() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("+9999999999")
                .password("SecurePassword123!")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientAuthService.login(request));
        assertEquals("Invalid email/phone or password", ex.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("jane.smith@email.com")
                .password("WrongPassword")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientAuthService.login(request));
        assertEquals("Invalid email/phone or password", ex.getMessage());
    }

    @Test
    void testLogin_InvalidIdentifierFormat() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("invalid-format")
                .password("SecurePassword123!")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientAuthService.login(request));
        assertEquals("Invalid email or phone number format", ex.getMessage());
    }

    @Test
    void testLogin_DeactivatedAccount() {
        // Deactivate the patient
        testPatient.setActive(false);
        patientRepository.save(testPatient);

        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("jane.smith@email.com")
                .password("SecurePassword123!")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientAuthService.login(request));
        assertEquals("Account is deactivated", ex.getMessage());
    }

    @Test
    void testJwtToken_GenerationAndValidation() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("jane.smith@email.com")
                .password("SecurePassword123!")
                .build();

        PatientLoginResponse response = patientAuthService.login(request);
        String token = response.getData().getAccess_token();

        // Validate token
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));

        // Extract claims
        assertEquals("jane.smith@email.com", jwtUtil.extractEmail(token));
        assertEquals(testPatient.getId(), jwtUtil.extractPatientId(token));
        assertEquals("patient", jwtUtil.extractRole(token));
    }

    @Test
    void testLogin_ExcludesSensitiveData() {
        PatientLoginRequest request = PatientLoginRequest.builder()
                .identifier("jane.smith@email.com")
                .password("SecurePassword123!")
                .build();

        PatientLoginResponse response = patientAuthService.login(request);
        PatientLoginResponse.PatientData patientData = response.getData().getPatient();

        // Verify sensitive data is not exposed
        assertNotNull(patientData.getAddress());
        assertNotNull(patientData.getEmergencyContact());
        assertNotNull(patientData.getInsuranceInfo());
        // Medical history should not be in response
        // Password hash should not be in response
    }
} 