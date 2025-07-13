package com.doubleo.adminservice.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.*;

import com.doubleo.adminservice.domain.admin.domain.Admin;
import com.doubleo.adminservice.domain.admin.dto.request.AdminPwUpdateRequest;
import com.doubleo.adminservice.domain.admin.dto.response.AdminInfoResponse;
import com.doubleo.adminservice.domain.admin.repository.AdminRepository;
import com.doubleo.adminservice.domain.admin.service.AdminServiceImpl;
import com.doubleo.adminservice.global.exception.CommonException;
import com.doubleo.adminservice.global.exception.errorcode.AdminErrorCode;
import com.doubleo.adminservice.global.util.TenantValidator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks private AdminServiceImpl adminService;

    @Mock private AdminRepository adminRepository;

    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock private TenantValidator<Admin> tenantValidator;

    private final String username = "test@test.com";
    private final String password = "password";
    private final String affiliation = "서울아산병원";
    private final String affiliationId = "000413";

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = Admin.createAdmin(username, "encoded", affiliation, affiliationId);
        ReflectionTestUtils.setField(admin, "id", 1L);
    }

    @Nested
    class getAdminInfo {

        @Test
        void 관리자정보_조회하면_정상적으로_반환된다() {
            // given
            given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
            given(tenantValidator.validateTenant(admin)).willReturn(admin);

            // when
            AdminInfoResponse response = adminService.getAdminInfo(admin.getId());

            // then
            assertThat(response.adminId()).isEqualTo(admin.getId());
            assertThat(response.username()).isEqualTo(admin.getUsername());
            assertThat(response.affiliation()).isEqualTo(admin.getAffiliation());
            assertThat(response.affiliationId()).isEqualTo(admin.getAffiliationId());
        }
    }

    @Nested
    class updateAdminPassword {

        @Test
        void 비밀번호_변경하면_정상적으로_변경된다() {
            // given
            String newPassword = "newPassword";
            String encodedNewPassword = "encodedNew";

            given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
            given(tenantValidator.validateTenant(admin)).willReturn(admin);

            given(bCryptPasswordEncoder.matches(password, "encoded")).willReturn(true);
            given(bCryptPasswordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // when
            adminService.updateAdminPassword(1L, new AdminPwUpdateRequest(password, newPassword));

            // then
            assertThat(admin.getPassword()).isEqualTo(encodedNewPassword);
        }

        @Test
        void 기존_비밀번호_유효하지_않으면_오류_발생한다() {
            // given
            given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
            given(tenantValidator.validateTenant(admin)).willReturn(admin);
            given(bCryptPasswordEncoder.matches("wrongPassword", "encoded")).willReturn(false);

            // when & then
            assertThatThrownBy(
                            () ->
                                    adminService.updateAdminPassword(
                                            1L,
                                            new AdminPwUpdateRequest(
                                                    "wrongPassword", "newPassword")))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(AdminErrorCode.INVALID_PASSWORD.getMessage());
        }

        @Test
        void 기존_비밀번호와_신규_비밀번호가_동일하면_오류_발생한다() {
            // given
            given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
            given(tenantValidator.validateTenant(admin)).willReturn(admin);
            given(bCryptPasswordEncoder.matches(password, "encoded")).willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    adminService.updateAdminPassword(
                                            1L, new AdminPwUpdateRequest(password, password)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(AdminErrorCode.DUPLICATED_PASSWORD.getMessage());
        }
    }
}
