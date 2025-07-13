package com.doubleo.adminservice.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponse(String accessToken, @JsonIgnore String refreshToken) {
    public static LoginResponse of(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
