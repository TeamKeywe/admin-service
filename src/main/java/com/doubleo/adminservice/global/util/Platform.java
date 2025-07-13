package com.doubleo.adminservice.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
    ADMIN("ADMIN");

    private final String platform;
}
