package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.AvailabilityRequest;
import com.thinkitve.aidemo.dto.AvailabilityResponse;
import com.thinkitve.aidemo.dto.AvailabilitySearchRequest;
import com.thinkitve.aidemo.entity.ProviderAvailability;
import com.thinkitve.aidemo.entity.AppointmentSlot;
import com.thinkitve.aidemo.entity.Location;
import com.thinkitve.aidemo.entity.Pricing;
import com.thinkitve.aidemo.repository.ProviderAvailabilityRepository;
import com.thinkitve.aidemo.repository.AppointmentSlotRepository;
import com.thinkitve.aidemo.util.TimezoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final ProviderAvailabilityRepository availabilityRepository;
    private final AppointmentSlotRepository slotRepository;
    private final TimezoneUtil timezoneUtil;

    @Override
    @Transactional
    public AvailabilityResponse createAvailability(AvailabilityRequest request) {
        // Validate time range
        if (!timezoneUtil.isTimeRangeValid(request.getStartTime(), request.getEndTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Validate timezone
        if (!timezoneUtil.isValidTimezone(request.getTimezone())) {
            throw new IllegalArgumentException("Invalid timezone: " + request.getTimezone());
        }

        UUID providerId = UUID.fromString(request.getProviderId());

        // Check for overlapping slots
        List<ProviderAvailability> overlappingSlots = availabilityRepository.findOverlappingSlots(
                providerId, request.getDate(), request.getStartTime(), request.getEndTime());

        if (!overlappingSlots.isEmpty()) {
            throw new IllegalArgumentException("Time slot conflicts with existing availability");
        }

        // Create availability entity
        ProviderAvailability availability = buildAvailabilityEntity(request, providerId);
        ProviderAvailability savedAvailability = availabilityRepository.save(availability);

        // Generate appointment slots
        List<AppointmentSlot> generatedSlots = generateAppointmentSlots(savedAvailability);
        slotRepository.saveAll(generatedSlots);

        // Handle recurring availability
        if (request.isRecurring() && request.getRecurrenceEndDate() != null) {
            createRecurringAvailability(request, providerId, savedAvailability);
        }

        return mapToResponse(savedAvailability, generatedSlots);
    }

    @Override
    public AvailabilityResponse getAvailability(UUID providerId, String startDate, String endDate, int page, int size) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        Pageable pageable = PageRequest.of(page, size);

        Page<ProviderAvailability> availabilities = availabilityRepository.findByProviderIdAndDateRange(
                providerId, start, end, pageable);

        List<AvailabilityResponse.AvailabilityData> data = availabilities.getContent().stream()
                .map(this::mapToAvailabilityData)
                .collect(Collectors.toList());

        return AvailabilityResponse.builder()
                .success(true)
                .message("Availability retrieved successfully")
                .data(null) // For pagination, we'll handle this differently
                .build();
    }

    @Override
    @Transactional
    public AvailabilityResponse updateAvailability(UUID slotId, AvailabilityRequest request) {
        ProviderAvailability availability = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        // Check for overlapping slots (excluding current slot)
        List<ProviderAvailability> overlappingSlots = availabilityRepository.findOverlappingSlotsExcludingId(
                availability.getProviderId(), request.getDate(), request.getStartTime(), 
                request.getEndTime(), slotId);

        if (!overlappingSlots.isEmpty()) {
            throw new IllegalArgumentException("Time slot conflicts with existing availability");
        }

        // Update availability
        updateAvailabilityEntity(availability, request);
        ProviderAvailability updatedAvailability = availabilityRepository.save(availability);

        // Regenerate appointment slots
        slotRepository.deleteByAvailabilityId(slotId);
        List<AppointmentSlot> newSlots = generateAppointmentSlots(updatedAvailability);
        slotRepository.saveAll(newSlots);

        return mapToResponse(updatedAvailability, newSlots);
    }

    @Override
    @Transactional
    public AvailabilityResponse deleteAvailability(UUID slotId, boolean deleteRecurring, String reason) {
        ProviderAvailability availability = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        // Check if there are existing appointments
        List<AppointmentSlot> existingSlots = slotRepository.findByAvailabilityId(slotId);
        boolean hasBookings = existingSlots.stream()
                .anyMatch(slot -> slot.getStatus() == AppointmentSlot.SlotStatus.BOOKED);

        if (hasBookings) {
            throw new IllegalArgumentException("Cannot delete availability with existing bookings");
        }

        if (deleteRecurring && availability.isRecurring()) {
            // Delete all recurring slots
            List<ProviderAvailability> recurringSlots = availabilityRepository
                    .findByProviderIdAndIsRecurringTrue(availability.getProviderId());
            availabilityRepository.deleteAll(recurringSlots);
        } else {
            // Delete only this slot
            availabilityRepository.delete(availability);
        }

        return AvailabilityResponse.builder()
                .success(true)
                .message("Availability deleted successfully")
                .build();
    }

    @Override
    public Page<AvailabilityResponse.AvailabilityData> searchAvailableSlots(AvailabilitySearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<ProviderAvailability> availabilities = availabilityRepository.findAvailableSlots(
                request.getStartDate(), request.getEndDate(), request.getAppointmentType(),
                request.isInsuranceAccepted() ? true : null, request.getMaxPrice(), pageable);

        return availabilities.map(this::mapToAvailabilityData);
    }

    private ProviderAvailability buildAvailabilityEntity(AvailabilityRequest request, UUID providerId) {
        return ProviderAvailability.builder()
                .providerId(providerId)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .timezone(request.getTimezone())
                .isRecurring(request.isRecurring())
                .recurrencePattern(request.getRecurrencePattern() != null ? 
                        ProviderAvailability.RecurrencePattern.valueOf(request.getRecurrencePattern().name()) : null)
                .recurrenceEndDate(request.getRecurrenceEndDate())
                .slotDuration(request.getSlotDuration())
                .breakDuration(request.getBreakDuration())
                .status(ProviderAvailability.AvailabilityStatus.valueOf(request.getStatus().name()))
                .maxAppointmentsPerSlot(request.getMaxAppointmentsPerSlot())
                .appointmentType(ProviderAvailability.AppointmentType.valueOf(request.getAppointmentType().name()))
                .location(buildLocation(request.getLocation()))
                .pricing(buildPricing(request.getPricing()))
                .notes(request.getNotes())
                .specialRequirements(request.getSpecialRequirements())
                .build();
    }

    private Location buildLocation(AvailabilityRequest.LocationDTO locationDTO) {
        return Location.builder()
                .type(ProviderAvailability.LocationType.valueOf(locationDTO.getType().name()))
                .address(locationDTO.getAddress())
                .roomNumber(locationDTO.getRoomNumber())
                .build();
    }

    private Pricing buildPricing(AvailabilityRequest.PricingDTO pricingDTO) {
        if (pricingDTO == null) return null;
        
        return Pricing.builder()
                .baseFee(pricingDTO.getBaseFee())
                .insuranceAccepted(pricingDTO.isInsuranceAccepted())
                .currency(pricingDTO.getCurrency())
                .build();
    }

    private List<AppointmentSlot> generateAppointmentSlots(ProviderAvailability availability) {
        List<AppointmentSlot> slots = new ArrayList<>();
        LocalTime currentTime = availability.getStartTime();
        
        while (currentTime.plusMinutes(availability.getSlotDuration()).isBefore(availability.getEndTime()) ||
               currentTime.plusMinutes(availability.getSlotDuration()).equals(availability.getEndTime())) {
            
            LocalTime slotEndTime = currentTime.plusMinutes(availability.getSlotDuration());
            
            // Convert to UTC for storage
            ZonedDateTime slotStartUTC = timezoneUtil.convertToUTC(
                    availability.getDate(), currentTime, availability.getTimezone());
            ZonedDateTime slotEndUTC = timezoneUtil.convertToUTC(
                    availability.getDate(), slotEndTime, availability.getTimezone());

            AppointmentSlot slot = AppointmentSlot.builder()
                    .availabilityId(availability.getId())
                    .providerId(availability.getProviderId())
                    .slotStartTime(slotStartUTC)
                    .slotEndTime(slotEndUTC)
                    .status(AppointmentSlot.SlotStatus.AVAILABLE)
                    .appointmentType(availability.getAppointmentType().name())
                    .build();

            slots.add(slot);
            
            // Add break duration
            currentTime = slotEndTime.plusMinutes(availability.getBreakDuration());
        }
        
        return slots;
    }

    private void createRecurringAvailability(AvailabilityRequest request, UUID providerId, 
                                           ProviderAvailability originalAvailability) {
        List<LocalDate> recurringDates = timezoneUtil.generateRecurringDates(
                request.getDate().plusDays(1), request.getRecurrenceEndDate(), 
                request.getRecurrencePattern().name(), 365); // Max 1 year

        for (LocalDate date : recurringDates) {
            ProviderAvailability recurringAvailability = ProviderAvailability.builder()
                    .providerId(providerId)
                    .date(date)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .timezone(request.getTimezone())
                    .isRecurring(true)
                    .recurrencePattern(ProviderAvailability.RecurrencePattern.valueOf(request.getRecurrencePattern().name()))
                    .recurrenceEndDate(request.getRecurrenceEndDate())
                    .slotDuration(request.getSlotDuration())
                    .breakDuration(request.getBreakDuration())
                    .status(ProviderAvailability.AvailabilityStatus.AVAILABLE)
                    .maxAppointmentsPerSlot(request.getMaxAppointmentsPerSlot())
                    .appointmentType(ProviderAvailability.AppointmentType.valueOf(request.getAppointmentType().name()))
                    .location(originalAvailability.getLocation())
                    .pricing(originalAvailability.getPricing())
                    .notes(request.getNotes())
                    .specialRequirements(request.getSpecialRequirements())
                    .build();

            ProviderAvailability saved = availabilityRepository.save(recurringAvailability);
            List<AppointmentSlot> slots = generateAppointmentSlots(saved);
            slotRepository.saveAll(slots);
        }
    }

    private void updateAvailabilityEntity(ProviderAvailability availability, AvailabilityRequest request) {
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setTimezone(request.getTimezone());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setBreakDuration(request.getBreakDuration());
        availability.setStatus(ProviderAvailability.AvailabilityStatus.valueOf(request.getStatus().name()));
        availability.setMaxAppointmentsPerSlot(request.getMaxAppointmentsPerSlot());
        availability.setAppointmentType(ProviderAvailability.AppointmentType.valueOf(request.getAppointmentType().name()));
        availability.setLocation(buildLocation(request.getLocation()));
        availability.setPricing(buildPricing(request.getPricing()));
        availability.setNotes(request.getNotes());
        availability.setSpecialRequirements(request.getSpecialRequirements());
    }

    private AvailabilityResponse mapToResponse(ProviderAvailability availability, List<AppointmentSlot> slots) {
        AvailabilityResponse.AvailabilityData data = mapToAvailabilityData(availability);
        data.setGeneratedSlots(slots.stream().map(this::mapToSlotDTO).collect(Collectors.toList()));

        return AvailabilityResponse.builder()
                .success(true)
                .message("Availability created successfully")
                .data(data)
                .build();
    }

    private AvailabilityResponse.AvailabilityData mapToAvailabilityData(ProviderAvailability availability) {
        return AvailabilityResponse.AvailabilityData.builder()
                .id(availability.getId())
                .providerId(availability.getProviderId().toString())
                .date(availability.getDate())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .timezone(availability.getTimezone())
                .isRecurring(availability.isRecurring())
                .recurrencePattern(availability.getRecurrencePattern() != null ? 
                        availability.getRecurrencePattern().name() : null)
                .recurrenceEndDate(availability.getRecurrenceEndDate())
                .slotDuration(availability.getSlotDuration())
                .breakDuration(availability.getBreakDuration())
                .status(availability.getStatus().name())
                .maxAppointmentsPerSlot(availability.getMaxAppointmentsPerSlot())
                .currentAppointments(availability.getCurrentAppointments())
                .appointmentType(availability.getAppointmentType().name())
                .location(mapToLocationDTO(availability.getLocation()))
                .pricing(mapToPricingDTO(availability.getPricing()))
                .notes(availability.getNotes())
                .specialRequirements(availability.getSpecialRequirements())
                .createdAt(availability.getCreatedAt())
                .updatedAt(availability.getUpdatedAt())
                .build();
    }

    private AvailabilityResponse.LocationDTO mapToLocationDTO(Location location) {
        if (location == null) return null;
        
        return AvailabilityResponse.LocationDTO.builder()
                .type(location.getType().name())
                .address(location.getAddress())
                .roomNumber(location.getRoomNumber())
                .build();
    }

    private AvailabilityResponse.PricingDTO mapToPricingDTO(Pricing pricing) {
        if (pricing == null) return null;
        
        return AvailabilityResponse.PricingDTO.builder()
                .baseFee(pricing.getBaseFee())
                .insuranceAccepted(pricing.isInsuranceAccepted())
                .currency(pricing.getCurrency())
                .build();
    }

    private AvailabilityResponse.AppointmentSlotDTO mapToSlotDTO(AppointmentSlot slot) {
        return AvailabilityResponse.AppointmentSlotDTO.builder()
                .id(slot.getId())
                .availabilityId(slot.getAvailabilityId())
                .providerId(slot.getProviderId().toString())
                .slotStartTime(slot.getSlotStartTime())
                .slotEndTime(slot.getSlotEndTime())
                .status(slot.getStatus().name())
                .patientId(slot.getPatientId() != null ? slot.getPatientId().toString() : null)
                .appointmentType(slot.getAppointmentType())
                .bookingReference(slot.getBookingReference())
                .createdAt(slot.getCreatedAt())
                .updatedAt(slot.getUpdatedAt())
                .build();
    }
} 