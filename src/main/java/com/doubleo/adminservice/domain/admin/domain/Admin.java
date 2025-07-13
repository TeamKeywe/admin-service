package com.doubleo.adminservice.domain.admin.domain;

import com.doubleo.adminservice.domain.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id", nullable = false)
    private Long id;

    @Column(
            name = "admin_username",
            columnDefinition = "VARCHAR(255) COLLATE utf8mb4_0900_as_cs",
            nullable = false,
            unique = true)
    private String username;

    @Column(name = "admin_password", nullable = false, length = 100)
    private String password;

    @Column(name = "admin_affiliation", nullable = false)
    private String affiliation;

    @Column(name = "admin_affiliation_id", nullable = false)
    private String affiliationId;

    @Builder(access = AccessLevel.PRIVATE)
    private Admin(String username, String password, String affiliation, String affiliationId) {
        this.username = username;
        this.password = password;
        this.affiliation = affiliation;
        this.affiliationId = affiliationId;
    }

    public static Admin createAdmin(
            String username, String password, String affiliation, String affiliationId) {
        return Admin.builder()
                .username(username)
                .password(password)
                .affiliation(affiliation)
                .affiliationId(affiliationId)
                .build();
    }

    public void updateAdminPassword(String passwordNew) {
        this.password = passwordNew;
    }
}
