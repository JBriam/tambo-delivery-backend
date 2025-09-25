package com.tambo.tambo_delivery_backend.auth.services;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.repositories.UserDetailRepository;
import com.tambo.tambo_delivery_backend.entities.Order;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${backend.base-url}")
    private String backendUrl;

    @Value("${frontend.base-url}")
    private String frontendUrl;

    public String sendEmail(User user) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("verificationCode", user.getVerificationCode());
            context.setVariable("baseUrl", backendUrl);

            String htmlContent = templateEngine.process("email-verification", context);

            helper.setFrom(sender, "Tambo Support");
            helper.setTo(user.getEmail());
            helper.setSubject("Verifica tu correo electrónico");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            return "Correo enviado con éxito";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar el correo de verificación: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error inesperado al enviar el correo: " + e.getMessage();
        }
    }

    public String sendResetPasswordEmail(User user, String token) {
        // Verifica si el correo de recuperación fue enviado en los últimos 1 minutos
        if (user.getLastPasswordResetRequest() != null &&
                (new Date().getTime() - user.getLastPasswordResetRequest().getTime()) < 60000) {
            return "Ya se envió un correo de recuperación recientemente. Intenta nuevamente en unos minutos.";
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("resetUrl", frontendUrl + "/v1/reset-password?token=" + token);

            String htmlContent = templateEngine.process("reset-password-email", context);

            helper.setFrom(sender, "Tambo Support");
            helper.setTo(user.getEmail());
            helper.setSubject("Recuperación de contraseña");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            // Guardamos la fecha de la peticion de reseteo
            user.setLastPasswordResetRequest(new Date());
            // Guardamos el token de reseteo de contraseña
            user.setResetToken(token);
            // El token expira en 30 minutos
            user.setResetTokenExpiry(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            userDetailRepository.save(user);

            return "Correo de recuperación enviado con éxito";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar el correo de recuperación: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error inesperado al enviar el correo: " + e.getMessage();
        }
    }

    // envía la boleta como PDF adjunto
    public String sendBoletaEmail(Order order, byte[] pdfBytes) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            try {
                helper.setFrom(sender, "Tambo Delivery");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                helper.setFrom(sender); // fallback sin nombre
            }
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Gracias por tu compra - Boleta Adjunta");

            String text = "Hola " + order.getUser().getUsername() + ",<br><br>"
                    + "Gracias por tu compra en Tambo Delivery. Adjuntamos tu boleta de compra en formato PDF.<br><br>"
                    + "Total pagado: <strong>S/ " + order.getTotalAmount() + "</strong><br>"
                    + "Fecha: " + order.getOrderDate() + "<br><br>"
                    + "Saludos cordiales,<br>Tambo Team";

            helper.setText(text, true); // true para HTML

            // Adjuntar PDF como archivo binario
            helper.addAttachment("Boleta_Tambo.pdf", new ByteArrayResource(pdfBytes));

            javaMailSender.send(mimeMessage);
            return "Boleta enviada correctamente";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar la boleta: " + e.getMessage();
        }
    }

    // envía la factura como PDF adjunto
    public String sendFacturaEmail(Order order, byte[] pdfBytes) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            try {
                helper.setFrom(sender, "Tambo Delivery");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                helper.setFrom(sender); // fallback sin nombre
            }
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Gracias por tu compra - Factura Adjunta");

            String text = "Hola " + order.getUser().getUsername() + ",<br><br>"
                    + "Gracias por tu compra en Tambo Delivery. Adjuntamos tu factura de compra en formato PDF.<br><br>"
                    + "Total pagado: <strong>S/ " + order.getTotalAmount() + "</strong><br>"
                    + "Fecha: " + order.getOrderDate() + "<br><br>"
                    + "Saludos cordiales,<br>Tambo Team";

            helper.setText(text, true); // true para HTML

            // Adjuntar PDF como archivo binario
            helper.addAttachment("Factura_Tambo.pdf", new ByteArrayResource(pdfBytes));

            javaMailSender.send(mimeMessage);
            return "Factura enviada correctamente";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar la factura: " + e.getMessage();
        }
    }

}