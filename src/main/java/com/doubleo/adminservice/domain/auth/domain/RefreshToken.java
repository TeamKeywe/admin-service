package com.doubleo.adminservice.domain.auth.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id private final Long adminId;

    private final String token;

    @TimeToLive private final long ttl;

    @Builder
    private RefreshToken(Long adminId, String token, long ttl) {
        this.adminId = adminId;
        this.token = token;
        this.ttl = ttl;
    }
}
