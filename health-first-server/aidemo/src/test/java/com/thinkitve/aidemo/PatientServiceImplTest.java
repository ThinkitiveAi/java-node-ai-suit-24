package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.PatientRegistrationRequest;
import com.thinkitve.aidemo.dto.PatientRegistrationResponse;
import com.thinkitve.aidemo.entity.Patient;
import com.thinkitve.aidemo.dto.PatientRegistrationRequest.Gender;
import com.thinkitve.aidemo.entity.Address;
import com.thinkitve.aidemo.entity.EmergencyContact;
import com.thinkitve.aidemo.entity.InsuranceInfo;
import com.thinkitve.aidemo.repository.PatientRepository;
import com.thinkitve.aidemo.service.PatientServiceImpl;
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
@Import({PatientServiceImpl.class, BCryptPasswordEncoder.class})
public class PatientServiceImplTest {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientServiceImpl patientService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private PatientRegistrationRequest.AddressDTO addressDTO;
    private PatientRegistrationRequest.EmergencyContactDTO emergencyContactDTO;
    private PatientRegistrationRequest.InsuranceInfoDTO insuranceInfoDTO;

    @BeforeEach
    void setup() {
        addressDTO = PatientRegistrationRequest.AddressDTO.builder()
                .street("456 Main Street")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .build();

        emergencyContactDTO = PatientRegistrationRequest.EmergencyContactDTO.builder()
                .name("John Smith")
                .phone("+1234567891")
                .relationship("spouse")
                .build();

        insuranceInfoDTO = PatientRegistrationRequest.InsuranceInfoDTO.builder()
                .provider("Blue Cross")
                .policyNumber("BC123456789")
                .build();
    }

    @Test
    void testRegisterPatient_Success() {
        PatientRegistrationRequest request = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.FEMALE)
                .address(addressDTO)
                .emergencyContact(emergencyContactDTO)
                .insuranceInfo(insuranceInfoDTO)
                .medicalHistory(Arrays.asList("Hypertension", "Diabetes"))
                .build();

        PatientRegistrationResponse response = patientService.registerPatient(request);

        assertTrue(response.isSuccess());
        assertEquals("Patient registered successfully", response.getMessage());
        assertNotNull(response.getData().getId());
        assertEquals("Jane", response.getData().getFirstName());
        assertEquals("jane.smith@email.com", response.getData().getEmail());
        assertFalse(response.getData().isEmailVerified());
        assertFalse(response.getData().isPhoneVerified());
        assertTrue(response.getData().isActive());

        // Verify password was hashed
        Patient saved = patientRepository.findById(response.getData().getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("SecurePassword123!", saved.getPasswordHash()));
    }

    @Test
    void testRegisterPatient_DuplicateEmail() {
        // Create first patient
        PatientRegistrationRequest request1 = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.FEMALE)
                .address(addressDTO)
                .build();

        patientService.registerPatient(request1);

        // Try to register with same email
        PatientRegistrationRequest request2 = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567891")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.FEMALE)
                .address(addressDTO)
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(request2));
        assertEquals("Email is already registered", ex.getMessage());
    }

    @Test
    void testRegisterPatient_DuplicatePhone() {
        // Create first patient
        PatientRegistrationRequest request1 = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.FEMALE)
                .address(addressDTO)
                .build();

        patientService.registerPatient(request1);

        // Try to register with same phone
        PatientRegistrationRequest request2 = PatientRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address(addressDTO)
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(request2));
        assertEquals("Phone number is already registered", ex.getMessage());
    }

    @Test
    void testRegisterPatient_PasswordMismatch() {
        PatientRegistrationRequest request = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("DifferentPassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address(addressDTO)
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(request));
        assertEquals("Password and confirmation do not match", ex.getMessage());
    }

    @Test
    void testRegisterPatient_WeakPassword() {
        PatientRegistrationRequest request = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("weak")
                .confirmPassword("weak")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address(addressDTO)
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(request));
        assertTrue(ex.getMessage().contains("Password must contain at least 8 characters"));
    }

    @Test
    void testRegisterPatient_Underage() {
        PatientRegistrationRequest request = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.now().minusYears(10)) // 10 years old
                .address(addressDTO)
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(request));
        assertEquals("Must be at least 13 years old", ex.getMessage());
    }

    @Test
    void testRegisterPatient_OptionalFields() {
        PatientRegistrationRequest request = PatientRegistrationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .phoneNumber("+1234567890")
                .password("SecurePassword123!")
                .confirmPassword("SecurePassword123!")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address(addressDTO)
                // No emergency contact or insurance info
                .build();

        PatientRegistrationResponse response = patientService.registerPatient(request);

        assertTrue(response.isSuccess());
        assertNull(response.getData().getEmergencyContact());
        assertNull(response.getData().getInsuranceInfo());
    }
} 