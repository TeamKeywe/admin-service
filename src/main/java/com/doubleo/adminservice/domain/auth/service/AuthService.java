package com.doubleo.adminservice.domain.auth.service;

import com.doubleo.adminservice.domain.auth.dto.request.LoginRequest;
import com.doubleo.adminservice.domain.auth.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse loginAdmin(LoginRequest request);

    void logoutAdmin(String accessTokenValue, Long adminId);
}
