package com.tambo.tambo_delivery_backend.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tambo.tambo_delivery_backend.auth.exceptions.RESTAuthenticationEntryPoint;
import com.tambo.tambo_delivery_backend.auth.helper.JWTTokenHelper;

// Configuración de seguridad de la aplicación Spring Boot que combina autenticación JWT y OAuth2
@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Habilita la configuración personalizada de seguridad web
public class WebSecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JWTTokenHelper jwtTokenHelper;

        @Autowired
        private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

        // Configuración del filtro de seguridad
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(withDefaults())

                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests((authorize) -> authorize
                                                // Endpoints solo para ADMIN (DEBE IR PRIMERO)
                                                //.requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                                                // Endpoints públicos
                                                .requestMatchers("/api/auth/**", "/oauth2/**", "/api/public/**", "/api/dev/**", "/api/admin/**")
                                                .permitAll()
                                                // Todos los demás endpoints requieren autenticación
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(restAuthenticationEntryPoint))
                                .oauth2Login((oauth2login) -> oauth2login
                                                .defaultSuccessUrl("/oauth2/success")
                                                .loginPage("/oauth2/authorization/google"))
                                .addFilterBefore(new JWTAuthenticationFilter(jwtTokenHelper, userDetailsService),
                                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        // Nota: Las rutas públicas se manejan directamente en SecurityFilterChain
        // No necesitamos WebSecurityCustomizer ya que SecurityFilterChain maneja todo

        // Configura el proveedor de autenticación con:
        // UserDetailsService (para cargar usuarios)
        // PasswordEncoder (para verificar contraseñas)
        @Bean
        public AuthenticationManager authenticationManager() {
                DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
                daoAuthenticationProvider.setUserDetailsService(userDetailsService);
                daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
                return new ProviderManager(daoAuthenticationProvider);
        }

        // Provee un encoder de contraseñas moderno y flexible
        @Bean
        public PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        // Configuración de CORS para permitir solicitudes desde el frontend
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedOrigin("http://localhost:4200"); // URL del frontend
                configuration.addAllowedMethod("*"); // GET, POST, OPTIONS, PUT, DELETE
                configuration.addAllowedHeader("*"); // Authorization, Content-Type, etc.
                configuration.setAllowCredentials(true); // Si usás cookies o auth headers
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
