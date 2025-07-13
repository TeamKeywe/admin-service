package com.doubleo.adminservice.domain.auth.service;

import com.doubleo.adminservice.domain.auth.dto.AccessTokenDto;
import com.doubleo.adminservice.domain.auth.dto.RefreshTokenDto;
import java.util.Optional;

public interface JwtTokenService {

    // AccessToken DTO 생성
    AccessTokenDto createAccessTokenDto(Long adminId, String tenantId);

    // AccessToken 생성
    String createAccessToken(Long adminId, String tenantId);

    // RefreshToken 생성
    String createRefreshToken(Long adminId);

    // DB 저장된 RefreshToken 조회 및 검증
    RefreshTokenDto retrieveRefreshToken(String refreshTokenValue);

    // AccessToken 만료 여부 검증 후 재발급
    Optional<AccessTokenDto> reissueAccessTokenIfExpired(String accessTokenValue);

    // 사용하지 않는 AccessToken BlackList 등록
    void putAccessTokenOnBlackList(String accessTokenValue);
}
