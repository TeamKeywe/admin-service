package com.doubleo.adminservice.domain.auth.controller;

import com.doubleo.adminservice.domain.auth.dto.RefreshTokenDto;
import com.doubleo.adminservice.domain.auth.dto.request.LoginRequest;
import com.doubleo.adminservice.domain.auth.dto.response.LoginResponse;
import com.doubleo.adminservice.domain.auth.service.AuthService;
import com.doubleo.adminservice.domain.auth.service.JwtTokenService;
import com.doubleo.adminservice.global.util.CookieUtil;
import com.doubleo.adminservice.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@Slf4j
@Tag(name = "1-2. Auth API", description = "관리자 로그인/로그아웃/Refresh Token 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenService jwtTokenService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "관리자 로그인", description = "관리자 로그인을 처리합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginAdmin(request);
        String refreshToken = response.refreshToken();
        HttpHeaders headers = cookieUtil.generateRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @Operation(summary = "관리자 로그아웃", description = "관리자 로그아웃을 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> adminLogout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestHeader("X-Admin-Id") Long adminId,
            HttpServletResponse response) {
        authService.logoutAdmin(authorizationHeader, adminId);
        HttpHeaders headers = cookieUtil.deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, headers.getFirst(HttpHeaders.SET_COOKIE));

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Access Token 재발급",
            description = "유효한 RefreshToken 을 통해 AccessToken 을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<Void> tokenReissue(
            HttpServletRequest request, HttpServletResponse response) {
        String oldAccessToken = jwtUtil.resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        String refreshToken = extractRefreshTokenFromCookie(request);

        log.info("oldAccessToken: {}, refreshToken: {}", oldAccessToken, refreshToken);
        RefreshTokenDto refreshTokenDto = jwtTokenService.retrieveRefreshToken(refreshToken);
        if (refreshTokenDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return jwtTokenService
                .reissueAccessTokenIfExpired(oldAccessToken)
                .map(
                        newToken -> {
                            // 새 토큰이 존재할 때 헤더에 담고 200 OK 리턴
                            response.setHeader(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + newToken.accessTokenValue());
                            return ResponseEntity.ok().<Void>build();
                        })
                // 없으면 204 No Content 리턴
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        return (cookie != null) ? cookie.getValue() : null;
    }
}
