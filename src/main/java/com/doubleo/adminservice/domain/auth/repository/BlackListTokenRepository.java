package com.doubleo.adminservice.domain.auth.repository;

import com.doubleo.adminservice.domain.auth.domain.BlackListToken;
import org.springframework.data.repository.CrudRepository;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, String> {}
