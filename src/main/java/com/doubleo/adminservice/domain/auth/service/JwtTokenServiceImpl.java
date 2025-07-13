package com.doubleo.adminservice.domain.auth.service;

import com.doubleo.adminservice.domain.auth.domain.BlackListToken;
import com.doubleo.adminservice.domain.auth.domain.RefreshToken;
import com.doubleo.adminservice.domain.auth.dto.AccessTokenDto;
import com.doubleo.adminservice.domain.auth.dto.RefreshTokenDto;
import com.doubleo.adminservice.domain.auth.repository.BlackListTokenRepository;
import com.doubleo.adminservice.domain.auth.repository.RefreshTokenRepository;
import com.doubleo.adminservice.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListTokenRepository blackListTokenRepository;

    public AccessTokenDto createAccessTokenDto(Long adminId, String tenantId) {
        return jwtUtil.generateAccessTokenDto(adminId, tenantId);
    }

    public String createAccessToken(Long adminId, String tenantId) {
        return jwtUtil.generateAccessToken(adminId, tenantId);
    }

    public Optional<AccessTokenDto> reissueAccessTokenIfExpired(String accessTokenValue) {
        try {
            jwtUtil.parseAccessToken(accessTokenValue); // 유효하면 재발급 불필요
            return Optional.empty();
        } catch (ExpiredJwtException e) {
            Long adminId = Long.parseLong(e.getClaims().getSubject());
            String tenantId = e.getClaims().get("tenantId", String.class);
            AccessTokenDto newToken = createAccessTokenDto(adminId, tenantId);
            return Optional.of(newToken);
        }
    }

    public void putAccessTokenOnBlackList(String accessTokenValue) {
        String accessToken = jwtUtil.resolveToken(accessTokenValue);
        if (accessToken == null) return;
        long remainingMs = jwtUtil.getRemainingExpirationMillis(accessToken);
        long ttlSeconds = remainingMs > 0 ? remainingMs / 1000 : 0;
        BlackListToken black = BlackListToken.createBlackListToken(accessToken, ttlSeconds);
        blackListTokenRepository.save(black);
    }

    public String createRefreshToken(Long adminId) {
        String token = jwtUtil.generateRefreshToken(adminId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .adminId(adminId)
                        .token(token)
                        .ttl(jwtUtil.getRefreshTokenExpirationTime())
                        .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public RefreshTokenDto retrieveRefreshToken(String refreshTokenValue) {
        RefreshTokenDto refreshTokenDto = jwtUtil.parseRefreshToken(refreshTokenValue);
        if (refreshTokenDto == null) return null;

        return refreshTokenRepository
                .findById(refreshTokenDto.adminId())
                .filter(token -> token.getToken().equals(refreshTokenDto.refreshTokenValue()))
                .map(t -> refreshTokenDto)
                .orElse(null);
    }
}
