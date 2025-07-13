package com.doubleo.adminservice.domain.admin.dto.response;

import com.doubleo.adminservice.domain.admin.domain.Admin;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdminInfoResponse(
        @Schema(description = "관리자 ID", example = "1") Long adminId,
        @Schema(description = "관리자 username", example = "admin1") String username,
        @Schema(description = "관리자 소속 병원", example = "서울아산병원") String affiliation,
        @Schema(description = "관리자 소속 병원 구분 ID", example = "TSEO09AS") String affiliationId) {
    public static AdminInfoResponse of(Admin admin) {
        return new AdminInfoResponse(
                admin.getId(),
                admin.getUsername(),
                admin.getAffiliation(),
                admin.getAffiliationId());
    }
}
