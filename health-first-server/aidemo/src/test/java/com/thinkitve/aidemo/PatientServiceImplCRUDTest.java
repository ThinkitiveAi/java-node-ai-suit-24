package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.PatientRegistrationRequest;
import com.thinkitve.aidemo.dto.PatientRegistrationResponse;
import com.thinkitve.aidemo.dto.PatientUpdateRequest;
import com.thinkitve.aidemo.dto.PatientListResponse;
import com.thinkitve.aidemo.entity.Patient;
import com.thinkitve.aidemo.entity.Patient.Gender;
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
public class PatientServiceImplCRUDTest {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientServiceImpl patientService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Patient testPatient;

    @BeforeEach
    void setup() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("Metropolis")
                .state("State")
                .zip("12345")
                .build();

        EmergencyContact emergencyContact = EmergencyContact.builder()
                .name("Jane Doe")
                .phone("+12345678902")
                .relationship("Spouse")
                .build();

        InsuranceInfo insuranceInfo = InsuranceInfo.builder()
                .provider("Health Insurance Co")
                .policyNumber("POL12345")
                .build();

        testPatient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12345678901")
                .passwordHash(passwordEncoder.encode("StrongP@ssw0rd"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address(address)
                .emergencyContact(emergencyContact)
                .medicalHistory(Arrays.asList("Hypertension", "Diabetes"))
                .insuranceInfo(insuranceInfo)
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();
        patientRepository.save(testPatient);
    }

    @Test
    void testGetAllPatients_Success() {
        PatientListResponse response = patientService.getAllPatients(null, null, null, null, 0, 20);

        assertTrue(response.isSuccess());
        assertEquals("Patients retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(1, response.getData().size());
        assertEquals("John", response.getData().get(0).getFirstName());
    }

    @Test
    void testGetAllPatients_WithFilters() {
        PatientListResponse response = patientService.getAllPatients("MALE", "Metropolis", "State", true, 0, 20);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("MALE", response.getData().get(0).getGender().toUpperCase());
    }

    @Test
    void testGetAllPatients_NoResults() {
        PatientListResponse response = patientService.getAllPatients("FEMALE", null, null, null, 0, 20);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetPatientById_Success() {
        PatientRegistrationResponse response = patientService.getPatientById(testPatient.getId());

        assertTrue(response.isSuccess());
        assertEquals("John", response.getData().getFirstName());
        assertEquals("john.doe@example.com", response.getData().getEmail());
        assertEquals("MALE", response.getData().getGender().toUpperCase());
    }

    @Test
    void testGetPatientById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> patientService.getPatientById(nonExistentId));
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void testUpdatePatient_Success() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .gender(PatientUpdateRequest.Gender.FEMALE)
                .address(PatientUpdateRequest.AddressDTO.builder()
                        .street("456 Oak St")
                        .city("New City")
                        .state("New State")
                        .zip("54321")
                        .build())
                .emergencyContact(PatientUpdateRequest.EmergencyContactDTO.builder()
                        .name("John Smith")
                        .phone("+12345678903")
                        .relationship("Spouse")
                        .build())
                .insuranceInfo(PatientUpdateRequest.InsuranceInfoDTO.builder()
                        .provider("New Insurance Co")
                        .policyNumber("POL67890")
                        .build())
                .notes("Updated patient information")
                .specialRequirements(Arrays.asList("Wheelchair accessible"))
                .build();

        PatientRegistrationResponse response = patientService.updatePatient(testPatient.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Jane", response.getData().getFirstName());
        assertEquals("Smith", response.getData().getLastName());
        assertEquals("FEMALE", response.getData().getGender().toUpperCase());
        assertEquals("456 Oak St", response.getData().getAddress().getStreet());
        assertEquals("John Smith", response.getData().getEmergencyContact().getName());
        assertEquals("New Insurance Co", response.getData().getInsuranceInfo().getProvider());
    }

    @Test
    void testUpdatePatient_PartialUpdate() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .firstName("Jane")
                .build();

        PatientRegistrationResponse response = patientService.updatePatient(testPatient.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Jane", response.getData().getFirstName());
        assertEquals("Doe", response.getData().getLastName()); // Should remain unchanged
        assertEquals("MALE", response.getData().getGender().toUpperCase()); // Should remain unchanged
    }

    @Test
    void testUpdatePatient_InvalidAge() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .dateOfBirth(LocalDate.now().minusYears(10)) // 10 years old
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> patientService.updatePatient(testPatient.getId(), updateRequest));
        assertEquals("Must be at least 13 years old", ex.getMessage());
    }

    @Test
    void testUpdatePatient_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .firstName("Jane")
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> patientService.updatePatient(nonExistentId, updateRequest));
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void testDeletePatient_Success() {
        PatientRegistrationResponse response = patientService.deletePatient(testPatient.getId());

        assertTrue(response.isSuccess());
        assertEquals("Patient deleted successfully", response.getMessage());

        // Verify soft delete - patient should still exist but be inactive
        assertTrue(patientRepository.findById(testPatient.getId()).isPresent());
        Patient deletedPatient = patientRepository.findById(testPatient.getId()).get();
        assertFalse(deletedPatient.isActive());
    }

    @Test
    void testDeletePatient_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> patientService.deletePatient(nonExistentId));
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void testDeletePatient_AlreadyDeleted() {
        // First delete
        patientService.deletePatient(testPatient.getId());

        // Try to delete again
        Exception ex = assertThrows(IllegalArgumentException.class, 
                () -> patientService.deletePatient(testPatient.getId()));
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void testUpdatePatient_AddressOnly() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .address(PatientUpdateRequest.AddressDTO.builder()
                        .street("789 Pine St")
                        .city("Updated City")
                        .state("Updated State")
                        .zip("98765")
                        .build())
                .build();

        PatientRegistrationResponse response = patientService.updatePatient(testPatient.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("789 Pine St", response.getData().getAddress().getStreet());
        assertEquals("Updated City", response.getData().getAddress().getCity());
        assertEquals("Updated State", response.getData().getAddress().getState());
        assertEquals("98765", response.getData().getAddress().getZip());
    }

    @Test
    void testUpdatePatient_EmergencyContactOnly() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .emergencyContact(PatientUpdateRequest.EmergencyContactDTO.builder()
                        .name("Updated Contact")
                        .phone("+12345678904")
                        .relationship("Parent")
                        .build())
                .build();

        PatientRegistrationResponse response = patientService.updatePatient(testPatient.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Updated Contact", response.getData().getEmergencyContact().getName());
        assertEquals("+12345678904", response.getData().getEmergencyContact().getPhone());
        assertEquals("Parent", response.getData().getEmergencyContact().getRelationship());
    }

    @Test
    void testUpdatePatient_InsuranceInfoOnly() {
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .insuranceInfo(PatientUpdateRequest.InsuranceInfoDTO.builder()
                        .provider("Updated Insurance")
                        .policyNumber("POL99999")
                        .build())
                .build();

        PatientRegistrationResponse response = patientService.updatePatient(testPatient.getId(), updateRequest);

        assertTrue(response.isSuccess());
        assertEquals("Updated Insurance", response.getData().getInsuranceInfo().getProvider());
        assertEquals("POL99999", response.getData().getInsuranceInfo().getPolicyNumber());
    }

    @Test
    void testPaginationInfo() {
        // Create additional patients for pagination testing
        for (int i = 1; i <= 25; i++) {
            Address address = Address.builder()
                    .street(i + " Test St")
                    .city("Test City")
                    .state("Test State")
                    .zip("12345")
                    .build();

            Patient patient = Patient.builder()
                    .firstName("Patient" + i)
                    .lastName("Test")
                    .email("patient" + i + "@test.com")
                    .phoneNumber("+1234567890" + i)
                    .passwordHash(passwordEncoder.encode("StrongP@ssw0rd"))
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .address(address)
                    .emailVerified(false)
                    .phoneVerified(false)
                    .isActive(true)
                    .build();
            patientRepository.save(patient);
        }

        PatientListResponse response = patientService.getAllPatients(null, null, null, null, 0, 10);

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