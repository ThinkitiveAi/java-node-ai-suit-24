package com.thinkitve.aidemo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private TokenData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenData {
        private String access_token;
        private long expires_in;
        private String token_type;
        private ProviderResponse provider;
    }
} 