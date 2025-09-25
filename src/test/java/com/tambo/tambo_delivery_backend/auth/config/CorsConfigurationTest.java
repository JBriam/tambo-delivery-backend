package com.tambo.tambo_delivery_backend.auth.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@SpringBootTest
public class CorsConfigurationTest {

        @Autowired
        private CorsConfigurationSource corsConfigurationSource;

        @Test
        public void testCorsConfiguration() {
                // Crear una solicitud simulada
                MockHttpServletRequest request = new MockHttpServletRequest();
                request.setMethod("GET");
                request.setServerName("localhost");
                request.setRequestURI("/api/test");

                // Obtener la configuración CORS para esta solicitud
                CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);

                assertNotNull(config, "La configuración CORS no debería ser nula");
                assertTrue(config.getAllowedOrigins().contains("http://localhost:4200"),
                                "Debería permitir el origen del frontend");
                assertTrue(config.getAllowedMethods().contains("*"),
                                "Debería permitir todos los métodos HTTP");
                assertTrue(config.getAllowedHeaders().contains("*"),
                                "Debería permitir todos los headers");
                assertTrue(config.getAllowCredentials(),
                                "Debería permitir credenciales");
        }
}