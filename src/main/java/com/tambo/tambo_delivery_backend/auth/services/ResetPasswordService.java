package com.tambo.tambo_delivery_backend.auth.services;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.repositories.UserDetailRepository;

@Service
public class ResetPasswordService {

    @Autowired
    UserDetailRepository userDetailRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        User user = userDetailRepository.findByResetToken(token);

        if (user == null || user.getResetTokenExpiry().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido o expirado.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userDetailRepository.save(user);

        return ResponseEntity.ok("Contraseña restablecida con éxito.");
    }

    public ResponseEntity<?> sendResetPasswordEmail(String email) {
        User user = userDetailRepository.findByEmail(email);

        if (user == null || !user.isEnabled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "El correo no está registrado o la cuenta no está activada."));
        }

        // Chequea si se puede enviar el correo (lógica incluida en el emailService)
        String result = emailService.sendResetPasswordEmail(user, UUID.randomUUID().toString());

        if (result.startsWith("Ya se envió")) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", result)); // código 429
        } else if (result.startsWith("Error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", result));
        }

        return ResponseEntity.ok(Map.of("message", result));
    }
}
