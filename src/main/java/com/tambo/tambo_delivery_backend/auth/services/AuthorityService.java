package com.tambo.tambo_delivery_backend.auth.services;

import com.tambo.tambo_delivery_backend.auth.entities.Authority;
import com.tambo.tambo_delivery_backend.auth.repositories.AuthorityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    // Proporciona la autoridad básica para usuarios normales
    public List<Authority> getUserAuthority() {
        List<Authority> authorities = new ArrayList<>();
        // Busca el rol con código "ADMIN" en la base de datos
        Authority authority = authorityRepository.findByRoleCode("USER");
        authorities.add(authority);
        // Lo devuelve dentro de una lista (formato que espera Spring Security)
        return authorities;
    }

    // Crear nuevos roles/permisos en el sistema
    public Authority createAuthority(String role, String description) {
        // role: Código del rol (ej. "ADMIN", "EDITOR")
        // description: Descripción legible del rol
        Authority authority = Authority.builder().roleCode(role).roleDescription(description).build();
        return authorityRepository.save(authority);
    }

    public List<Authority> getAllRoles() {
        return authorityRepository.findAll();
    }

    public Authority getRoleById(UUID id) {
        try {
            Authority role = authorityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            return role;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener el rol", e);
        }
    }

    public Authority updateRole(UUID id, String roleCode, String description) {
        try {
            Authority role = authorityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            role.setRoleCode(roleCode);
            role.setRoleDescription(description);
            authorityRepository.save(role);
            return role;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el rol", e);
        }
    }

    public void deleteRole(UUID id) {

        try {
            authorityRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al eliminar el rol", e);
        }
    }

    // Proporciona la lista de autoridades
    public List<Authority> getRequestedAuthorities(List<String> roles) {

        try {
            List<Authority> authorities = new ArrayList<>();

            for (String roleCode : roles) {
                Authority authority = authorityRepository.findByRoleCode(roleCode);

                authorities.add(authority);
            }

            System.out.println(authorities);

            return authorities;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
}
