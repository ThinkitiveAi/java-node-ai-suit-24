package com.thinkitve.aidemo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {
    private boolean success;
    private String message;
    private AvailabilityData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailabilityData {
        private UUID id;
        private String providerId;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String timezone;
        private boolean isRecurring;
        private String recurrencePattern;
        private LocalDate recurrenceEndDate;
        private int slotDuration;
        private int breakDuration;
        private String status;
        private int maxAppointmentsPerSlot;
        private int currentAppointments;
        private String appointmentType;
        private LocationDTO location;
        private PricingDTO pricing;
        private String notes;
        private List<String> specialRequirements;
        private List<AppointmentSlotDTO> generatedSlots;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationDTO {
        private String type;
        private String address;
        private String roomNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingDTO {
        private BigDecimal baseFee;
        private boolean insuranceAccepted;
        private String currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentSlotDTO {
        private UUID id;
        private UUID availabilityId;
        private String providerId;
        private ZonedDateTime slotStartTime;
        private ZonedDateTime slotEndTime;
        private String status;
        private String patientId;
        private String appointmentType;
        private String bookingReference;
        private Instant createdAt;
        private Instant updatedAt;
    }
} 