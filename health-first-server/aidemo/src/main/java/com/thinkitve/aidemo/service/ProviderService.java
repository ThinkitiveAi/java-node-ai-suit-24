package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.ProviderRegistrationRequest;
import com.thinkitve.aidemo.dto.ProviderRegistrationResponse;
import com.thinkitve.aidemo.dto.ProviderUpdateRequest;
import com.thinkitve.aidemo.dto.ProviderListResponse;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface ProviderService {
    ProviderRegistrationResponse registerProvider(ProviderRegistrationRequest request);
    ProviderListResponse getAllProviders(String specialization, String city, String state, Boolean isActive, int page, int size);
    ProviderRegistrationResponse getProviderById(UUID id);
    ProviderRegistrationResponse updateProvider(UUID id, ProviderUpdateRequest request);
    ProviderRegistrationResponse deleteProvider(UUID id);
} 