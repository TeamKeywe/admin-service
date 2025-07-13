package com.doubleo.adminservice.domain.admin.controller;

import com.doubleo.adminservice.domain.admin.dto.request.AdminPwUpdateRequest;
import com.doubleo.adminservice.domain.admin.dto.response.AdminInfoResponse;
import com.doubleo.adminservice.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1-1. Admin API", description = "관리자 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "관리자 본인 정보 조회", description = "관리자 본인 정보를 조회합니다.")
    @GetMapping("/me")
    public AdminInfoResponse adminGet(@RequestHeader("X-Admin-Id") Long adminId) {
        return adminService.getAdminInfo(adminId);
    }

    @Operation(summary = "관리자 비밀번호 업데이트", description = "관리자 비밀번호를 업데이트합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> adminPasswordUpdate(
            @RequestHeader("X-Admin-Id") Long adminId,
            @Valid @RequestBody AdminPwUpdateRequest request) {
        adminService.updateAdminPassword(adminId, request);
        return ResponseEntity.ok().build();
    }
}
