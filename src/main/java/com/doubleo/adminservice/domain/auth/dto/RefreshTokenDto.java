package com.doubleo.adminservice.domain.auth.dto;

public record RefreshTokenDto(Long adminId, String refreshTokenValue, Long ttl) {
    public static RefreshTokenDto of(Long adminId, String refreshTokenValue, Long ttl) {
        return new RefreshTokenDto(adminId, refreshTokenValue, ttl);
    }
}
