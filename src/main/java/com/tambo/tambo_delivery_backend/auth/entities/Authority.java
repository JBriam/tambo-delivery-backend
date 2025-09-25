package com.tambo.tambo_delivery_backend.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Table(name = "AUTH_AUTHORITY", uniqueConstraints = {
        @UniqueConstraint(columnNames = "roleCode", name = "UK_AUTH_AUTHORITY_CODE")
})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String roleCode;

    @Column(nullable = false, length = 100)
    private String roleDescription;

    @Override
    public String getAuthority() {
        return roleCode;
    }
}
