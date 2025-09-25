package com.tambo.tambo_delivery_backend.auth.repositories;

import com.tambo.tambo_delivery_backend.auth.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findByRoleCode(String user);

    boolean existsByRoleCode(String roleCode);
}
