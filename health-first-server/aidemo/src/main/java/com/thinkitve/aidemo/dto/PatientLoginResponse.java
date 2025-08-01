package com.thinkitve.aidemo.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientLoginResponse {
    private boolean success;
    private String message;
    private TokenData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenData {
        private String access_token;
        private long expires_in;
        private String token_type;
        private PatientData patient;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientData {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private AddressDTO address;
        private EmergencyContactDTO emergencyContact;
        private InsuranceInfoDTO insuranceInfo;
        private boolean emailVerified;
        private boolean phoneVerified;
        private boolean isActive;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDTO {
        private String street;
        private String city;
        private String state;
        private String zip;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmergencyContactDTO {
        private String name;
        private String phone;
        private String relationship;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsuranceInfoDTO {
        private String provider;
        private String policyNumber;
    }
} 