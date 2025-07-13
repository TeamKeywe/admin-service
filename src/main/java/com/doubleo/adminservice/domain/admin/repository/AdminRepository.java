package com.doubleo.adminservice.domain.admin.repository;

import com.doubleo.adminservice.domain.admin.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findAdminByUsername(String username);
}
