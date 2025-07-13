package com.doubleo.adminservice.global.util;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public HttpHeaders generateRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from("refreshToken", refreshToken)
                        .path("/")
                        .secure(false)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders deleteRefreshTokenCookie() {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from("refreshToken", "")
                        .path("/")
                        .maxAge(0)
                        .secure(false)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }
}
