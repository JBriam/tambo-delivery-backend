package com.tambo.tambo_delivery_backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El email no puede estar vacío.")
    private String userName;
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;
}
