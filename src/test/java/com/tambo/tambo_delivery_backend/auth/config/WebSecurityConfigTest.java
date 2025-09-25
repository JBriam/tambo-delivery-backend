package com.tambo.tambo_delivery_backend.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSecurityConfigTest {

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    public void testSecurityFilterChainBean() {
        assertNotNull(securityFilterChain, "El SecurityFilterChain debería estar configurado");
    }

    @Test
    public void testJwtFilterAdded() {
        // Verifica que el filtro JWT esté en la cadena de filtros
        boolean jwtFilterPresent = securityFilterChain.getFilters().stream()
                .anyMatch(filter -> filter instanceof AuthorizationFilter);

        assertTrue(jwtFilterPresent, "El filtro JWT debería estar presente");
    }

    @Test
    public void testPasswordEncoderBean() {
        assertNotNull(webSecurityConfig.passwordEncoder(), "El PasswordEncoder debería estar configurado");
    }

    @Test
    public void testAuthenticationManagerBean() {
        assertNotNull(webSecurityConfig.authenticationManager(), "El AuthenticationManager debería estar configurado");
    }

    @Test
    public void testCorsConfigurationSourceBean() {
        assertNotNull(webSecurityConfig.corsConfigurationSource(), "La configuración CORS debería estar presente");
    }
}