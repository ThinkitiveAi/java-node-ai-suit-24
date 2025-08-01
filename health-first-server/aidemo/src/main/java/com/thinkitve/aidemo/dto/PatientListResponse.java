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
public class PatientListResponse {
    private boolean success;
    private String message;
    private List<PatientData> data;
    private PaginationInfo pagination;

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
        private PatientRegistrationRequest.AddressDTO address;
        private PatientRegistrationRequest.EmergencyContactDTO emergencyContact;
        private PatientRegistrationRequest.InsuranceInfoDTO insuranceInfo;
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
    public static class PaginationInfo {
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;
    }
} 