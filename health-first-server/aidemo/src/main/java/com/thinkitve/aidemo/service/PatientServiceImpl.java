package com.thinkitve.aidemo.service;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation do not match");
        }

        if (!isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Password must contain at least 8 characters, including uppercase, lowercase, number, and special character");
        }

        if (!isAgeValid(request.getDateOfBirth())) {
            throw new IllegalArgumentException("Must be at least 13 years old");
        }

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        Address address = Address.builder()
                .street(request.getAddress().getStreet())
                .city(request.getAddress().getCity())
                .state(request.getAddress().getState())
                .zip(request.getAddress().getZip())
                .build();

        EmergencyContact emergencyContact = null;
        if (request.getEmergencyContact() != null && 
            request.getEmergencyContact().getName() != null && 
            !request.getEmergencyContact().getName().trim().isEmpty()) {
            emergencyContact = EmergencyContact.builder()
                    .name(request.getEmergencyContact().getName())
                    .phone(request.getEmergencyContact().getPhone())
                    .relationship(request.getEmergencyContact().getRelationship())
                    .build();
        }

        InsuranceInfo insuranceInfo = null;
        if (request.getInsuranceInfo() != null && 
            request.getInsuranceInfo().getProvider() != null && 
            !request.getInsuranceInfo().getProvider().trim().isEmpty()) {
            insuranceInfo = InsuranceInfo.builder()
                    .provider(request.getInsuranceInfo().getProvider())
                    .policyNumber(request.getInsuranceInfo().getPolicyNumber())
                    .build();
        }

        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(Patient.Gender.valueOf(request.getGender().name()))
                .address(address)
                .emergencyContact(emergencyContact)
                .medicalHistory(request.getMedicalHistory())
                .insuranceInfo(insuranceInfo)
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Override
    public PatientListResponse getAllPatients(String gender, String city, String state, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patients = patientRepository.findPatientsWithFilters(gender, city, state, isActive, pageable);
        
        List<PatientListResponse.PatientData> patientDataList = patients.getContent().stream()
                .map(this::mapToPatientListData)
                .collect(Collectors.toList());

        PatientListResponse.PaginationInfo paginationInfo = PatientListResponse.PaginationInfo.builder()
                .totalElements(patients.getTotalElements())
                .totalPages(patients.getTotalPages())
                .currentPage(patients.getNumber())
                .pageSize(patients.getSize())
                .hasNext(patients.hasNext())
                .hasPrevious(patients.hasPrevious())
                .build();

        return PatientListResponse.builder()
                .success(true)
                .message("Patients retrieved successfully")
                .data(patientDataList)
                .pagination(paginationInfo)
                .build();
    }

    @Override
    public PatientRegistrationResponse getPatientById(UUID id) {
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        return mapToResponse(patient);
    }

    @Override
    @Transactional
    public PatientRegistrationResponse updatePatient(UUID id, PatientUpdateRequest request) {
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // Update fields if provided
        if (request.getFirstName() != null) {
            patient.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            patient.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            if (!isAgeValid(request.getDateOfBirth())) {
                throw new IllegalArgumentException("Must be at least 13 years old");
            }
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            patient.setGender(Gender.valueOf(request.getGender().name()));
        }
        if (request.getAddress() != null) {
            updateAddress(patient, request.getAddress());
        }
        if (request.getEmergencyContact() != null) {
            updateEmergencyContact(patient, request.getEmergencyContact());
        }
        if (request.getInsuranceInfo() != null) {
            updateInsuranceInfo(patient, request.getInsuranceInfo());
        }
        if (request.getNotes() != null) {
            patient.setNotes(request.getNotes());
        }
        if (request.getSpecialRequirements() != null) {
            patient.setSpecialRequirements(request.getSpecialRequirements());
        }

        Patient updated = patientRepository.save(patient);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public PatientRegistrationResponse deletePatient(UUID id) {
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // Soft delete - set isActive to false
        patient.setActive(false);
        patientRepository.save(patient);

        return PatientRegistrationResponse.builder()
                .success(true)
                .message("Patient deleted successfully")
                .build();
    }

    private boolean isPasswordStrong(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private boolean isAgeValid(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        Period age = Period.between(dateOfBirth, today);
        return age.getYears() >= 13;
    }

    private void updateAddress(Patient patient, PatientUpdateRequest.AddressDTO addressDTO) {
        Address currentAddress = patient.getAddress();
        Address updatedAddress = Address.builder()
                .street(addressDTO.getStreet() != null ? addressDTO.getStreet() : currentAddress.getStreet())
                .city(addressDTO.getCity() != null ? addressDTO.getCity() : currentAddress.getCity())
                .state(addressDTO.getState() != null ? addressDTO.getState() : currentAddress.getState())
                .zip(addressDTO.getZip() != null ? addressDTO.getZip() : currentAddress.getZip())
                .build();
        patient.setAddress(updatedAddress);
    }

    private void updateEmergencyContact(Patient patient, PatientUpdateRequest.EmergencyContactDTO contactDTO) {
        EmergencyContact currentContact = patient.getEmergencyContact();
        EmergencyContact updatedContact = EmergencyContact.builder()
                .name(contactDTO.getName() != null ? contactDTO.getName() : 
                      (currentContact != null ? currentContact.getName() : null))
                .phone(contactDTO.getPhone() != null ? contactDTO.getPhone() : 
                       (currentContact != null ? currentContact.getPhone() : null))
                .relationship(contactDTO.getRelationship() != null ? contactDTO.getRelationship() : 
                             (currentContact != null ? currentContact.getRelationship() : null))
                .build();
        patient.setEmergencyContact(updatedContact);
    }

    private void updateInsuranceInfo(Patient patient, PatientUpdateRequest.InsuranceInfoDTO insuranceDTO) {
        InsuranceInfo currentInsurance = patient.getInsuranceInfo();
        InsuranceInfo updatedInsurance = InsuranceInfo.builder()
                .provider(insuranceDTO.getProvider() != null ? insuranceDTO.getProvider() : 
                         (currentInsurance != null ? currentInsurance.getProvider() : null))
                .policyNumber(insuranceDTO.getPolicyNumber() != null ? insuranceDTO.getPolicyNumber() : 
                             (currentInsurance != null ? currentInsurance.getPolicyNumber() : null))
                .build();
        patient.setInsuranceInfo(updatedInsurance);
    }

    private PatientRegistrationResponse mapToResponse(Patient patient) {
        PatientRegistrationResponse.PatientData patientData = PatientRegistrationResponse.PatientData.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender().name().toLowerCase())
                .address(PatientRegistrationResponse.AddressDTO.builder()
                        .street(patient.getAddress().getStreet())
                        .city(patient.getAddress().getCity())
                        .state(patient.getAddress().getState())
                        .zip(patient.getAddress().getZip())
                        .build())
                .emailVerified(patient.isEmailVerified())
                .phoneVerified(patient.isPhoneVerified())
                .isActive(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();

        if (patient.getEmergencyContact() != null) {
            patientData.setEmergencyContact(PatientRegistrationResponse.EmergencyContactDTO.builder()
                    .name(patient.getEmergencyContact().getName())
                    .phone(patient.getEmergencyContact().getPhone())
                    .relationship(patient.getEmergencyContact().getRelationship())
                    .build());
        }

        if (patient.getInsuranceInfo() != null) {
            patientData.setInsuranceInfo(PatientRegistrationResponse.InsuranceInfoDTO.builder()
                    .provider(patient.getInsuranceInfo().getProvider())
                    .policyNumber(patient.getInsuranceInfo().getPolicyNumber())
                    .build());
        }

        return PatientRegistrationResponse.builder()
                .success(true)
                .message("Patient registered successfully")
                .data(patientData)
                .build();
    }

    private PatientListResponse.PatientData mapToPatientListData(Patient patient) {
        PatientListResponse.PatientData patientData = PatientListResponse.PatientData.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender().name().toLowerCase())
                .address(PatientRegistrationRequest.AddressDTO.builder()
                        .street(patient.getAddress().getStreet())
                        .city(patient.getAddress().getCity())
                        .state(patient.getAddress().getState())
                        .zip(patient.getAddress().getZip())
                        .build())
                .emailVerified(patient.isEmailVerified())
                .phoneVerified(patient.isPhoneVerified())
                .isActive(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();

        if (patient.getEmergencyContact() != null) {
            patientData.setEmergencyContact(PatientRegistrationRequest.EmergencyContactDTO.builder()
                    .name(patient.getEmergencyContact().getName())
                    .phone(patient.getEmergencyContact().getPhone())
                    .relationship(patient.getEmergencyContact().getRelationship())
                    .build());
        }

        if (patient.getInsuranceInfo() != null) {
            patientData.setInsuranceInfo(PatientRegistrationRequest.InsuranceInfoDTO.builder()
                    .provider(patient.getInsuranceInfo().getProvider())
                    .policyNumber(patient.getInsuranceInfo().getPolicyNumber())
                    .build());
        }

        return patientData;
    }
} 