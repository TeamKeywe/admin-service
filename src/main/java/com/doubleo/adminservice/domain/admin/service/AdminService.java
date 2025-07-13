package com.doubleo.adminservice.domain.admin.service;

import com.doubleo.adminservice.domain.admin.dto.request.AdminPwUpdateRequest;
import com.doubleo.adminservice.domain.admin.dto.response.AdminInfoResponse;

public interface AdminService {

    AdminInfoResponse getAdminInfo(Long adminId);

    void updateAdminPassword(Long adminId, AdminPwUpdateRequest request);
}
