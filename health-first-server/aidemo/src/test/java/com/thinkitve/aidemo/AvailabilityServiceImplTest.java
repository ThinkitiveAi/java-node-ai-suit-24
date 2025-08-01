package com.thinkitve.aidemo;

import com.thinkitve.aidemo.dto.AvailabilityRequest;
import com.thinkitve.aidemo.dto.AvailabilityResponse;
import com.thinkitve.aidemo.entity.ProviderAvailability;
import com.thinkitve.aidemo.entity.AppointmentSlot;
import com.thinkitve.aidemo.entity.Location;
import com.thinkitve.aidemo.entity.Pricing;
import com.thinkitve.aidemo.repository.ProviderAvailabilityRepository;
import com.thinkitve.aidemo.repository.AppointmentSlotRepository;
import com.thinkitve.aidemo.service.AvailabilityServiceImpl;
import com.thinkitve.aidemo.util.TimezoneUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AvailabilityServiceImpl.class, TimezoneUtil.class})
public class AvailabilityServiceImplTest {
    @Autowired
    private ProviderAvailabilityRepository availabilityRepository;
    @Autowired
    private AppointmentSlotRepository slotRepository;
    @Autowired
    private AvailabilityServiceImpl availabilityService;
    @Autowired
    private TimezoneUtil timezoneUtil;

    private UUID testProviderId;

    @BeforeEach
    void setup() {
        testProviderId = UUID.randomUUID();
    }

    @Test
    void testCreateAvailability_Success() {
        AvailabilityRequest request = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .timezone("America/New_York")
                .slotDuration(30)
                .breakDuration(15)
                .maxAppointmentsPerSlot(1)
                .appointmentType(AvailabilityRequest.AppointmentType.CONSULTATION)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .roomNumber("101")
                        .build())
                .pricing(AvailabilityRequest.PricingDTO.builder()
                        .baseFee(new BigDecimal("100.00"))
                        .insuranceAccepted(true)
                        .currency("USD")
                        .build())
                .notes("Regular consultation hours")
                .specialRequirements(Arrays.asList("Wheelchair accessible"))
                .build();

        AvailabilityResponse response = availabilityService.createAvailability(request);

        assertTrue(response.isSuccess());
        assertEquals("Availability created successfully", response.getMessage());
        assertNotNull(response.getData().getId());
        assertEquals(testProviderId.toString(), response.getData().getProviderId());
        assertEquals("America/New_York", response.getData().getTimezone());
        assertEquals(30, response.getData().getSlotDuration());
        assertNotNull(response.getData().getGeneratedSlots());
        assertFalse(response.getData().getGeneratedSlots().isEmpty());
    }

    @Test
    void testCreateAvailability_InvalidTimeRange() {
        AvailabilityRequest request = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(17, 0))
                .endTime(LocalTime.of(9, 0)) // End before start
                .timezone("America/New_York")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> availabilityService.createAvailability(request));
        assertEquals("End time must be after start time", ex.getMessage());
    }

    @Test
    void testCreateAvailability_InvalidTimezone() {
        AvailabilityRequest request = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .timezone("Invalid/Timezone")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> availabilityService.createAvailability(request));
        assertTrue(ex.getMessage().contains("Invalid timezone"));
    }

    @Test
    void testCreateAvailability_OverlappingSlots() {
        // Create first availability
        AvailabilityRequest request1 = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .timezone("America/New_York")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        availabilityService.createAvailability(request1);

        // Try to create overlapping availability
        AvailabilityRequest request2 = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(11, 0)) // Overlaps with first
                .endTime(LocalTime.of(14, 0))
                .timezone("America/New_York")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> availabilityService.createAvailability(request2));
        assertEquals("Time slot conflicts with existing availability", ex.getMessage());
    }

    @Test
    void testCreateAvailability_Recurring() {
        AvailabilityRequest request = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .timezone("America/New_York")
                .isRecurring(true)
                .recurrencePattern(AvailabilityRequest.RecurrencePattern.WEEKLY)
                .recurrenceEndDate(LocalDate.of(2024, 2, 15))
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        AvailabilityResponse response = availabilityService.createAvailability(request);

        assertTrue(response.isSuccess());
        
        // Verify recurring slots were created
        long recurringCount = availabilityRepository.findByProviderIdAndIsRecurringTrue(testProviderId).size();
        assertTrue(recurringCount > 1); // Should have multiple weekly slots
    }

    @Test
    void testUpdateAvailability_Success() {
        // Create initial availability
        AvailabilityRequest createRequest = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .timezone("America/New_York")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        AvailabilityResponse createResponse = availabilityService.createAvailability(createRequest);
        UUID availabilityId = createResponse.getData().getId();

        // Update availability
        AvailabilityRequest updateRequest = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(10, 0)) // Changed start time
                .endTime(LocalTime.of(18, 0)) // Changed end time
                .timezone("America/New_York")
                .slotDuration(45) // Changed slot duration
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        AvailabilityResponse updateResponse = availabilityService.updateAvailability(availabilityId, updateRequest);

        assertTrue(updateResponse.isSuccess());
        assertEquals(45, updateResponse.getData().getSlotDuration());
        assertEquals(LocalTime.of(10, 0), updateResponse.getData().getStartTime());
        assertEquals(LocalTime.of(18, 0), updateResponse.getData().getEndTime());
    }

    @Test
    void testDeleteAvailability_Success() {
        // Create availability
        AvailabilityRequest request = AvailabilityRequest.builder()
                .providerId(testProviderId.toString())
                .date(LocalDate.of(2024, 1, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .timezone("America/New_York")
                .slotDuration(30)
                .location(AvailabilityRequest.LocationDTO.builder()
                        .type(AvailabilityRequest.LocationType.CLINIC)
                        .address("123 Main St")
                        .build())
                .build();

        AvailabilityResponse createResponse = availabilityService.createAvailability(request);
        UUID availabilityId = createResponse.getData().getId();

        // Delete availability
        AvailabilityResponse deleteResponse = availabilityService.deleteAvailability(availabilityId, false, "No longer needed");

        assertTrue(deleteResponse.isSuccess());
        assertEquals("Availability deleted successfully", deleteResponse.getMessage());
    }

    @Test
    void testTimezoneUtil_ValidTimezone() {
        assertTrue(timezoneUtil.isValidTimezone("America/New_York"));
        assertTrue(timezoneUtil.isValidTimezone("Europe/London"));
        assertFalse(timezoneUtil.isValidTimezone("Invalid/Timezone"));
    }

    @Test
    void testTimezoneUtil_TimeRangeValidation() {
        assertTrue(timezoneUtil.isTimeRangeValid(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        assertFalse(timezoneUtil.isTimeRangeValid(LocalTime.of(17, 0), LocalTime.of(9, 0)));
        assertFalse(timezoneUtil.isTimeRangeValid(LocalTime.of(9, 0), LocalTime.of(9, 0)));
    }

    @Test
    void testTimezoneUtil_RecurringDates() {
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 29);
        
        var weeklyDates = timezoneUtil.generateRecurringDates(startDate, endDate, "WEEKLY", 10);
        assertEquals(3, weeklyDates.size()); // 15th, 22nd, 29th
        
        var dailyDates = timezoneUtil.generateRecurringDates(startDate, endDate, "DAILY", 10);
        assertEquals(15, dailyDates.size()); // 15 days
    }
} 