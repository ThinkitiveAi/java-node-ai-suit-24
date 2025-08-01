package com.thinkitve.aidemo.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderRegistrationResponse {
    private boolean success;
    private String message;
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private ClinicAddressDTO clinicAddress;
    private String verificationStatus;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClinicAddressDTO {
        private String street;
        private String city;
        private String state;
        private String zip;
    }
} 