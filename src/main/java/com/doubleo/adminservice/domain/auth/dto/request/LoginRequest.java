package com.doubleo.adminservice.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "관리자 아이디", example = "username") String username,
        @Schema(description = "관리자 패스워드", example = "pw12345") String password) {}
