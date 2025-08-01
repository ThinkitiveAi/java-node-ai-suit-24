package com.thinkitve.aidemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlot {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "availability_id", nullable = false)
    @NotNull(message = "Availability ID is required")
    private UUID availabilityId;

    @Column(name = "provider_id", nullable = false)
    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    @Column(name = "slot_start_time", nullable = false)
    @NotNull(message = "Slot start time is required")
    private ZonedDateTime slotStartTime;

    @Column(name = "slot_end_time", nullable = false)
    @NotNull(message = "Slot end time is required")
    private ZonedDateTime slotEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "appointment_type", length = 50)
    @Size(max = 50, message = "Appointment type cannot exceed 50 characters")
    private String appointmentType;

    @Column(name = "booking_reference", unique = true, length = 100)
    @Size(max = 100, message = "Booking reference cannot exceed 100 characters")
    private String bookingReference;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum SlotStatus {
        AVAILABLE, BOOKED, CANCELLED, BLOCKED
    }
} 