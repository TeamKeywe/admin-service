package com.doubleo.adminservice.domain.auth.dto;

public record AccessTokenDto(Long adminId, String tenantId, String accessTokenValue) {
    public static AccessTokenDto of(Long adminId, String tenantId, String accessTokenValue) {
        return new AccessTokenDto(adminId, tenantId, accessTokenValue);
    }
}
