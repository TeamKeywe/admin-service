package com.doubleo.adminservice.global.util;

import com.doubleo.adminservice.domain.auth.dto.AccessTokenDto;
import com.doubleo.adminservice.domain.auth.dto.RefreshTokenDto;
import com.doubleo.adminservice.infra.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public AccessTokenDto generateAccessTokenDto(Long adminId, String tenantId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.accessTokenExpirationMilliTime());
        String tokenValue = buildAccessToken(adminId, tenantId, issuedAt, expiredAt);
        return new AccessTokenDto(adminId, tenantId, tokenValue);
    }

    public String generateAccessToken(Long adminId, String tenantId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.accessTokenExpirationMilliTime());
        return buildAccessToken(adminId, tenantId, issuedAt, expiredAt);
    }

    //    public RefreshTokenDto generateRefreshTokenDto(Long adminId) {
    //        Date issuedAt = new Date();
    //        Date expiredAt =
    //                new Date(issuedAt.getTime() +
    // jwtProperties.refreshTokenExpirationMilliTime());
    //        String tokenValue = buildRefreshToken(adminId, issuedAt, expiredAt);
    //        return new RefreshTokenDto(
    //                adminId, tokenValue, jwtProperties.refreshTokenExpirationTime());
    //    }

    public String resolveToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }
        return null;
    }

    public long getRemainingExpirationMillis(String tokenValue) {
        Jws<Claims> claims = getClaims(tokenValue, getAccessTokenKey());
        Date exp = claims.getBody().getExpiration();
        return Math.max(exp.getTime() - System.currentTimeMillis(), 0);
    }

    public AccessTokenDto parseAccessToken(String accessTokenValue) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = getClaims(accessTokenValue, getAccessTokenKey());
            Long adminId = Long.parseLong(claims.getBody().getSubject());
            String tenantId = claims.getBody().get("tenantId", String.class);
            return new AccessTokenDto(adminId, accessTokenValue, tenantId);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    public RefreshTokenDto parseRefreshToken(String refreshTokenValue) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = getClaims(refreshTokenValue, getRefreshTokenKey());

            return RefreshTokenDto.of(
                    Long.parseLong(claims.getBody().getSubject()),
                    refreshTokenValue,
                    jwtProperties.refreshTokenExpirationTime());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    public String generateRefreshToken(Long adminId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.refreshTokenExpirationMilliTime());
        return buildRefreshToken(adminId, issuedAt, expiredAt);
    }

    public long getRefreshTokenExpirationTime() {
        return jwtProperties.refreshTokenExpirationTime();
    }

    private Key getAccessTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret().getBytes());
    }

    private Key getRefreshTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret().getBytes());
    }

    private String buildAccessToken(Long adminId, String tenantId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(adminId.toString())
                .claim("tenantId", tenantId)
                .claim("platform", Platform.ADMIN.name())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getAccessTokenKey())
                .compact();
    }

    private String buildRefreshToken(Long adminId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(adminId.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getRefreshTokenKey())
                .compact();
    }

    private Jws<Claims> getClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .requireIssuer(jwtProperties.issuer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
