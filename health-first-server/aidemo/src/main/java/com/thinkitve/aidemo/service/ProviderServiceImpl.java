package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.ProviderRegistrationRequest;
import com.thinkitve.aidemo.dto.ProviderRegistrationResponse;
import com.thinkitve.aidemo.dto.ProviderUpdateRequest;
import com.thinkitve.aidemo.dto.ProviderListResponse;
import com.thinkitve.aidemo.entity.Provider;
import com.thinkitve.aidemo.entity.Provider.VerificationStatus;
import com.thinkitve.aidemo.entity.ClinicAddress;
import com.thinkitve.aidemo.repository.ProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ProviderServiceImpl implements ProviderService {
    private final ProviderRepository providerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProviderRegistrationResponse registerProvider(@Valid ProviderRegistrationRequest request) {
        if (providerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (providerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        if (providerRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new IllegalArgumentException("License number already registered");
        }
        if (!isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }
        Provider provider = Provider.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .yearsOfExperience(request.getYearsOfExperience())
                .clinicAddress(ClinicAddress.builder()
                        .street(request.getClinicAddress().getStreet())
                        .city(request.getClinicAddress().getCity())
                        .state(request.getClinicAddress().getState())
                        .zip(request.getClinicAddress().getZip())
                        .build())
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
        Provider saved = providerRepository.save(provider);
        return mapToResponse(saved);
    }

    @Override
    public ProviderListResponse getAllProviders(String specialization, String city, String state, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Provider> providers = providerRepository.findProvidersWithFilters(specialization, city, state, isActive, pageable);
        
        List<ProviderListResponse.ProviderData> providerDataList = providers.getContent().stream()
                .map(this::mapToProviderListData)
                .collect(Collectors.toList());

        ProviderListResponse.PaginationInfo paginationInfo = ProviderListResponse.PaginationInfo.builder()
                .totalElements(providers.getTotalElements())
                .totalPages(providers.getTotalPages())
                .currentPage(providers.getNumber())
                .pageSize(providers.getSize())
                .hasNext(providers.hasNext())
                .hasPrevious(providers.hasPrevious())
                .build();

        return ProviderListResponse.builder()
                .success(true)
                .message("Providers retrieved successfully")
                .data(providerDataList)
                .pagination(paginationInfo)
                .build();
    }

    @Override
    public ProviderRegistrationResponse getProviderById(UUID id) {
        Provider provider = providerRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return mapToResponse(provider);
    }

    @Override
    @Transactional
    public ProviderRegistrationResponse updateProvider(UUID id, ProviderUpdateRequest request) {
        Provider provider = providerRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        // Validate phone number uniqueness if being updated
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(provider.getPhoneNumber())) {
            if (providerRepository.findByPhoneNumberExcludingId(request.getPhoneNumber(), id).isPresent()) {
                throw new IllegalArgumentException("Phone number already registered");
            }
            provider.setPhoneNumber(request.getPhoneNumber());
        }

        // Update fields if provided
        if (request.getFirstName() != null) {
            provider.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            provider.setLastName(request.getLastName());
        }
        if (request.getSpecialization() != null) {
            provider.setSpecialization(request.getSpecialization());
        }
        if (request.getYearsOfExperience() != null) {
            provider.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (request.getClinicAddress() != null) {
            updateClinicAddress(provider, request.getClinicAddress());
        }

        Provider updated = providerRepository.save(provider);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public ProviderRegistrationResponse deleteProvider(UUID id) {
        Provider provider = providerRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        // Soft delete - set isActive to false
        provider.setActive(false);
        providerRepository.save(provider);

        return ProviderRegistrationResponse.builder()
                .success(true)
                .message("Provider deleted successfully")
                .build();
    }

    private boolean isPasswordStrong(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private void updateClinicAddress(Provider provider, ProviderUpdateRequest.ClinicAddressDTO addressDTO) {
        ClinicAddress currentAddress = provider.getClinicAddress();
        ClinicAddress updatedAddress = ClinicAddress.builder()
                .street(addressDTO.getStreet() != null ? addressDTO.getStreet() : currentAddress.getStreet())
                .city(addressDTO.getCity() != null ? addressDTO.getCity() : currentAddress.getCity())
                .state(addressDTO.getState() != null ? addressDTO.getState() : currentAddress.getState())
                .zip(addressDTO.getZip() != null ? addressDTO.getZip() : currentAddress.getZip())
                .build();
        provider.setClinicAddress(updatedAddress);
    }

    private ProviderRegistrationResponse mapToResponse(Provider provider) {
        return ProviderRegistrationResponse.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(provider.getEmail())
                .phoneNumber(provider.getPhoneNumber())
                .specialization(provider.getSpecialization())
                .licenseNumber(provider.getLicenseNumber())
                .yearsOfExperience(provider.getYearsOfExperience())
                .clinicAddress(ProviderRegistrationResponse.ClinicAddressDTO.builder()
                        .street(provider.getClinicAddress().getStreet())
                        .city(provider.getClinicAddress().getCity())
                        .state(provider.getClinicAddress().getState())
                        .zip(provider.getClinicAddress().getZip())
                        .build())
                .verificationStatus(provider.getVerificationStatus().name().toLowerCase())
                .isActive(provider.isActive())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }

    private ProviderListResponse.ProviderData mapToProviderListData(Provider provider) {
        return ProviderListResponse.ProviderData.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(provider.getEmail())
                .phoneNumber(provider.getPhoneNumber())
                .specialization(provider.getSpecialization())
                .licenseNumber(provider.getLicenseNumber())
                .yearsOfExperience(provider.getYearsOfExperience())
                .clinicAddress(ProviderRegistrationRequest.ClinicAddressDTO.builder()
                        .street(provider.getClinicAddress().getStreet())
                        .city(provider.getClinicAddress().getCity())
                        .state(provider.getClinicAddress().getState())
                        .zip(provider.getClinicAddress().getZip())
                        .build())
                .verificationStatus(provider.getVerificationStatus().name().toLowerCase())
                .isActive(provider.isActive())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }
} 