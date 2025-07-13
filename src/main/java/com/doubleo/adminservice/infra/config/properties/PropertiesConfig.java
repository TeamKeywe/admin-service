package com.doubleo.adminservice.infra.config.properties;

import com.doubleo.adminservice.infra.config.jwt.JwtProperties;
import com.doubleo.adminservice.infra.config.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class, RedisProperties.class})
@Configuration
public class PropertiesConfig {}
