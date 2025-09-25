package com.tambo.tambo_delivery_backend.auth.helper;

import java.util.Random;

// generar códigos de verificación numéricos
public class VerificationCodeGenerator {

    // Produce números entre 100000 y 999999
    public static String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
