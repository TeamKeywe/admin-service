package com.doubleo.adminservice.domain.admin.service;

import com.doubleo.adminservice.domain.admin.domain.Admin;
import com.doubleo.adminservice.domain.admin.dto.request.AdminPwUpdateRequest;
import com.doubleo.adminservice.domain.admin.dto.response.AdminInfoResponse;
import com.doubleo.adminservice.domain.admin.repository.AdminRepository;
import com.doubleo.adminservice.global.exception.CommonException;
import com.doubleo.adminservice.global.exception.errorcode.AdminErrorCode;
import com.doubleo.adminservice.global.util.TenantValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantValidator<Admin> tenantValidator;

    @Override
    public AdminInfoResponse getAdminInfo(Long adminId) {
        Admin admin = findAdmin(adminId);
        return AdminInfoResponse.of(admin);
    }

    public void updateAdminPassword(Long adminId, AdminPwUpdateRequest request) {
        Admin admin = findAdmin(adminId);
        validateAdminPassword(request.passwordOriginal(), admin.getPassword());
        isPasswordNew(request.passwordNew(), admin.getPassword());
        admin.updateAdminPassword(passwordEncoder.encode(request.passwordNew()));
    }

    // util
    private Admin findAdmin(Long adminId) {
        return tenantValidator.validateTenant(
                adminRepository
                        .findById(adminId)
                        .orElseThrow(() -> new CommonException(AdminErrorCode.ADMIN_NOT_FOUND)));
    }

    private void validateAdminPassword(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new CommonException(AdminErrorCode.INVALID_PASSWORD);
        }
    }

    private void isPasswordNew(String raw, String encoded) {
        if (passwordEncoder.matches(raw, encoded)) {
            throw new CommonException(AdminErrorCode.DUPLICATED_PASSWORD);
        }
    }
}
