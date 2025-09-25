package com.tambo.tambo_delivery_backend.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tambo.tambo_delivery_backend.auth.helper.JWTTokenHelper;

import java.io.IOException;

//  Interceptar las solicitudes HTTP y validar los tokens JWT para autenticar usuarios.
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService; // cargar los detalles del usuario desde la base de datos
    private final JWTTokenHelper jwtTokenHelper; // Utilidad para trabajar con tokens JWT

    public JWTAuthenticationFilter(JWTTokenHelper jwtTokenHelper, UserDetailsService userDetailsService) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no comienza con "Bearer", pasa la solicitud al siguiente
        // filtro
        if (null == authHeader || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authToken = jwtTokenHelper.getToken(request); // Extrae el token
            if (null != authToken) {
                String userName = jwtTokenHelper.getUserNameFromToken(authToken); // Obtiene el nombre de usuario del
                                                                                  // token
                if (null != userName) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // Carga los detalles del
                                                                                               // usuario

                    // Valida el token contra los detalles del usuario
                    if (jwtTokenHelper.validateToken(authToken, userDetails)) {
                        // Crea y establece la autenticación en el contexto de seguridad
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }

            }
            // pasa la solicitud al siguiente filtro
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
