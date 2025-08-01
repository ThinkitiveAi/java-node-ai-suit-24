package com.thinkitve.aidemo.repository;

import com.thinkitve.aidemo.entity.ProviderAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, UUID> {
    
    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.providerId = :providerId " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "ORDER BY pa.date, pa.startTime")
    Page<ProviderAvailability> findByProviderIdAndDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.providerId = :providerId " +
           "AND pa.date = :date " +
           "AND pa.status = 'AVAILABLE' " +
           "ORDER BY pa.startTime")
    List<ProviderAvailability> findAvailableByProviderAndDate(
            @Param("providerId") UUID providerId,
            @Param("date") LocalDate date);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.providerId = :providerId " +
           "AND pa.date = :date " +
           "AND ((pa.startTime <= :endTime AND pa.endTime >= :startTime) " +
           "OR (pa.startTime >= :startTime AND pa.startTime < :endTime) " +
           "OR (pa.endTime > :startTime AND pa.endTime <= :endTime))")
    List<ProviderAvailability> findOverlappingSlots(
            @Param("providerId") UUID providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.providerId = :providerId " +
           "AND pa.date = :date " +
           "AND pa.id != :excludeId " +
           "AND ((pa.startTime <= :endTime AND pa.endTime >= :startTime) " +
           "OR (pa.startTime >= :startTime AND pa.startTime < :endTime) " +
           "OR (pa.endTime > :startTime AND pa.endTime <= :endTime))")
    List<ProviderAvailability> findOverlappingSlotsExcludingId(
            @Param("providerId") UUID providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") UUID excludeId);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.status = 'AVAILABLE' " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND (:appointmentType IS NULL OR pa.appointmentType = :appointmentType) " +
           "AND (:insuranceAccepted IS NULL OR pa.pricing.insuranceAccepted = :insuranceAccepted) " +
           "AND (:maxPrice IS NULL OR pa.pricing.baseFee <= :maxPrice) " +
           "ORDER BY pa.date, pa.startTime")
    Page<ProviderAvailability> findAvailableSlots(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("appointmentType") String appointmentType,
            @Param("insuranceAccepted") Boolean insuranceAccepted,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable);

    List<ProviderAvailability> findByProviderIdAndIsRecurringTrue(UUID providerId);
} 