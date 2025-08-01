package com.thinkitve.aidemo.repository;

import com.thinkitve.aidemo.entity.AppointmentSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    
    @Query("SELECT as FROM AppointmentSlot as WHERE as.providerId = :providerId " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotEndTime <= :endTime " +
           "AND as.status = 'AVAILABLE' " +
           "ORDER BY as.slotStartTime")
    List<AppointmentSlot> findAvailableSlotsByProviderAndTimeRange(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.availabilityId = :availabilityId " +
           "ORDER BY as.slotStartTime")
    List<AppointmentSlot> findByAvailabilityId(@Param("availabilityId") UUID availabilityId);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.patientId = :patientId " +
           "AND as.slotStartTime >= :startTime " +
           "ORDER BY as.slotStartTime")
    Page<AppointmentSlot> findByPatientIdAndFutureSlots(
            @Param("patientId") UUID patientId,
            @Param("startTime") ZonedDateTime startTime,
            Pageable pageable);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.status = 'AVAILABLE' " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotEndTime <= :endTime " +
           "AND (:appointmentType IS NULL OR as.appointmentType = :appointmentType) " +
           "ORDER BY as.slotStartTime")
    Page<AppointmentSlot> findAvailableSlotsInTimeRange(
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime,
            @Param("appointmentType") String appointmentType,
            Pageable pageable);

    boolean existsByBookingReference(String bookingReference);

    void deleteByAvailabilityId(UUID slotId);
} 