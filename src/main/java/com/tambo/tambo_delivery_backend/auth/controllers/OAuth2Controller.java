package com.tambo.tambo_delivery_backend.auth.controllers;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.helper.JWTTokenHelper;
import com.tambo.tambo_delivery_backend.auth.services.OAuth2Service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

// maneja la autenticación OAuth2
@RestController
@CrossOrigin // Permite solicitudes CORS desde otros dominios (frontend).
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    @GetMapping("/success")
    public void callbackOAuth2(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response)
            throws IOException {

        // Guardar el email
        String userName = oAuth2User.getAttribute("email");
        // Buscar un usuario existente por email
        User user = oAuth2Service.getUser(userName);
        // Crear un nuevo usuario si no existe
        if (user == null) {
            user = oAuth2Service.createUser(oAuth2User, "google");
        }

        // Genera un token JWT usando JWTTokenHelper
        String token = jwtTokenHelper.generateToken(user.getUsername());

        // Redirige al frontend (React en localhost:4200) con el
        // token como parámetro de consulta.
        response.sendRedirect("http://localhost:4200/oauth2/callback?token=" + token);

    }
}
