package com.doubleo.adminservice.domain.auth.service;

import com.doubleo.adminservice.domain.admin.domain.Admin;
import com.doubleo.adminservice.domain.admin.repository.AdminRepository;
import com.doubleo.adminservice.domain.auth.dto.request.LoginRequest;
import com.doubleo.adminservice.domain.auth.dto.response.LoginResponse;
import com.doubleo.adminservice.domain.auth.repository.RefreshTokenRepository;
import com.doubleo.adminservice.global.exception.CommonException;
import com.doubleo.adminservice.global.exception.errorcode.AdminErrorCode;
import com.doubleo.adminservice.global.util.TenantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final BCryptPasswordEncoder encoder;
    private final TenantValidator<Admin> tenantValidator;

    public LoginResponse loginAdmin(LoginRequest request) {
        Admin admin = validateAdminByEmail(request.username());
        if (!encoder.matches(request.password(), admin.getPassword())) {
            throw new CommonException(AdminErrorCode.ADMIN_NOT_FOUND);
        }
        return getLoginResponse(admin);
    }

    public void logoutAdmin(String accessTokenValue, Long adminId) {
        validateAdminById(adminId);
        refreshTokenRepository.deleteById(adminId);
        jwtTokenService.putAccessTokenOnBlackList(accessTokenValue);
    }

    private Admin validateAdminByEmail(String email) {
        return adminRepository
                .findAdminByUsername(email)
                .orElseThrow(() -> new CommonException(AdminErrorCode.ADMIN_NOT_FOUND));
    }

    private void validateAdminById(Long adminId) {
        tenantValidator.validateTenant(
                adminRepository
                        .findById(adminId)
                        .orElseThrow(() -> new CommonException(AdminErrorCode.ADMIN_NOT_FOUND)));
    }

    private LoginResponse getLoginResponse(Admin admin) {
        String accessToken = jwtTokenService.createAccessToken(admin.getId(), admin.getTenantId());
        String refreshToken = jwtTokenService.createRefreshToken(admin.getId());
        return LoginResponse.of(accessToken, refreshToken);
    }
}
