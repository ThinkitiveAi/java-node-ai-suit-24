package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.PatientLoginRequest;
import com.thinkitve.aidemo.dto.PatientLoginResponse;
import com.thinkitve.aidemo.entity.Patient;
import com.thinkitve.aidemo.entity.Address;
import com.thinkitve.aidemo.entity.EmergencyContact;
import com.thinkitve.aidemo.entity.InsuranceInfo;
import com.thinkitve.aidemo.repository.PatientRepository;
import com.thinkitve.aidemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PatientAuthServiceImpl implements PatientAuthService {
    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{7,14}$");

    @Override
    public PatientLoginResponse login(PatientLoginRequest request) {
        // Validate identifier format
        String identifier = request.getIdentifier().trim();
        boolean isEmail = EMAIL_PATTERN.matcher(identifier).matches();
        boolean isPhone = PHONE_PATTERN.matcher(identifier).matches();

        if (!isEmail && !isPhone) {
            throw new IllegalArgumentException("Invalid email or phone number format");
        }

        // Find patient by email or phone
        Optional<Patient> patientOpt;
        if (isEmail) {
            patientOpt = patientRepository.findByEmail(identifier);
        } else {
            patientOpt = patientRepository.findByPhoneNumber(identifier);
        }

        Patient patient = patientOpt.orElseThrow(() -> 
            new IllegalArgumentException("Invalid email/phone or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), patient.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email/phone or password");
        }

        // Check if patient is active
        if (!patient.isActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        // Generate JWT token with 30-minute expiry
        Map<String, Object> claims = new HashMap<>();
        claims.put("patient_id", patient.getId().toString());
        claims.put("email", patient.getEmail());
        claims.put("role", "patient");

        String token = jwtUtil.generateToken(claims, patient.getEmail(), 1800); // 30 minutes

        // Map patient to response (excluding sensitive data)
        PatientLoginResponse.PatientData patientData = mapToPatientData(patient);

        // Build token data
        PatientLoginResponse.TokenData tokenData = PatientLoginResponse.TokenData.builder()
                .access_token(token)
                .expires_in(1800) // 30 minutes in seconds
                .token_type("Bearer")
                .patient(patientData)
                .build();

        return PatientLoginResponse.builder()
                .success(true)
                .message("Login successful")
                .data(tokenData)
                .build();
    }

    private PatientLoginResponse.PatientData mapToPatientData(Patient patient) {
        PatientLoginResponse.PatientData patientData = PatientLoginResponse.PatientData.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .emailVerified(patient.isEmailVerified())
                .phoneVerified(patient.isPhoneVerified())
                .isActive(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();

        // Add address
        if (patient.getAddress() != null) {
            patientData.setAddress(PatientLoginResponse.AddressDTO.builder()
                    .street(patient.getAddress().getStreet())
                    .city(patient.getAddress().getCity())
                    .state(patient.getAddress().getState())
                    .zip(patient.getAddress().getZip())
                    .build());
        }

        // Add emergency contact if present
        if (patient.getEmergencyContact() != null) {
            patientData.setEmergencyContact(PatientLoginResponse.EmergencyContactDTO.builder()
                    .name(patient.getEmergencyContact().getName())
                    .phone(patient.getEmergencyContact().getPhone())
                    .relationship(patient.getEmergencyContact().getRelationship())
                    .build());
        }

        // Add insurance info if present
        if (patient.getInsuranceInfo() != null) {
            patientData.setInsuranceInfo(PatientLoginResponse.InsuranceInfoDTO.builder()
                    .provider(patient.getInsuranceInfo().getProvider())
                    .policyNumber(patient.getInsuranceInfo().getPolicyNumber())
                    .build());
        }

        return patientData;
    }
} 