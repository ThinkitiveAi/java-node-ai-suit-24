package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.AvailabilityRequest;
import com.thinkitve.aidemo.dto.AvailabilityResponse;
import com.thinkitve.aidemo.dto.AvailabilitySearchRequest;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface AvailabilityService {
    AvailabilityResponse createAvailability(AvailabilityRequest request);
    AvailabilityResponse getAvailability(UUID providerId, String startDate, String endDate, int page, int size);
    AvailabilityResponse updateAvailability(UUID slotId, AvailabilityRequest request);
    AvailabilityResponse deleteAvailability(UUID slotId, boolean deleteRecurring, String reason);
    Page<AvailabilityResponse.AvailabilityData> searchAvailableSlots(AvailabilitySearchRequest request);
} 