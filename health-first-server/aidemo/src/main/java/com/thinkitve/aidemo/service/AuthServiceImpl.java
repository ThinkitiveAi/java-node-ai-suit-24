package com.thinkitve.aidemo.service;

import com.thinkitve.aidemo.dto.LoginRequest;
import com.thinkitve.aidemo.dto.LoginResponse;
import com.thinkitve.aidemo.dto.ProviderResponse;
import com.thinkitve.aidemo.entity.Provider;
import com.thinkitve.aidemo.repository.ProviderRepository;
import com.thinkitve.aidemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final ProviderRepository providerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // Find provider by email
        Provider provider = providerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), provider.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Check if provider is active
        if (!provider.isActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                provider.getId(),
                provider.getEmail(),
                provider.getSpecialization()
        );

        // Map provider to response (excluding password)
        ProviderResponse providerResponse = mapToProviderResponse(provider);

        // Build token data
        LoginResponse.TokenData tokenData = LoginResponse.TokenData.builder()
                .access_token(token)
                .expires_in(3600) // 1 hour in seconds
                .token_type("Bearer")
                .provider(providerResponse)
                .build();

        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .data(tokenData)
                .build();
    }

    private ProviderResponse mapToProviderResponse(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(provider.getEmail())
                .phoneNumber(provider.getPhoneNumber())
                .specialization(provider.getSpecialization())
                .licenseNumber(provider.getLicenseNumber())
                .yearsOfExperience(provider.getYearsOfExperience())
                .clinicAddress(ProviderResponse.ClinicAddressDTO.builder()
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