package com.doubleo.adminservice.domain.auth.repository;

import com.doubleo.adminservice.domain.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {}
