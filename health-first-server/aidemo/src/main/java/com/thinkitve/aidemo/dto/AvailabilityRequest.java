package com.thinkitve.aidemo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRequest {
    @NotNull(message = "Provider ID is required")
    private String providerId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Timezone is required")
    @Pattern(regexp = "^[A-Za-z_]+/[A-Za-z_]+$", message = "Invalid timezone format")
    private String timezone;

    private boolean isRecurring = false;

    private RecurrencePattern recurrencePattern;

    private LocalDate recurrenceEndDate;

    @Min(value = 10, message = "Slot duration must be at least 10 minutes")
    @Max(value = 120, message = "Slot duration must not exceed 120 minutes")
    private int slotDuration = 30;

    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 60, message = "Break duration must not exceed 60 minutes")
    private int breakDuration = 0;

    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    @Min(value = 1, message = "Max appointments per slot must be at least 1")
    @Max(value = 10, message = "Max appointments per slot must not exceed 10")
    private int maxAppointmentsPerSlot = 1;

    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @NotNull(message = "Location is required")
    private LocationDTO location;

    private PricingDTO pricing;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private List<String> specialRequirements;

    public enum RecurrencePattern {
        DAILY, WEEKLY, MONTHLY
    }

    public enum AvailabilityStatus {
        AVAILABLE, BOOKED, CANCELLED, BLOCKED, MAINTENANCE
    }

    public enum AppointmentType {
        CONSULTATION, FOLLOW_UP, EMERGENCY, TELEMEDICINE
    }

    public enum LocationType {
        CLINIC, HOSPITAL, TELEMEDICINE, HOME_VISIT
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationDTO {
        @NotNull(message = "Location type is required")
        private LocationType type;

        @Size(max = 500, message = "Address cannot exceed 500 characters")
        private String address;

        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        private String roomNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingDTO {
        @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
        private BigDecimal baseFee;

        private boolean insuranceAccepted = false;

        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        private String currency = "USD";
    }
} 