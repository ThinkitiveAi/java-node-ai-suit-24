package com.thinkitve.aidemo.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRegistrationResponse {
    private boolean success;
    private String message;
    private PatientData data;

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
        private String gender;
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