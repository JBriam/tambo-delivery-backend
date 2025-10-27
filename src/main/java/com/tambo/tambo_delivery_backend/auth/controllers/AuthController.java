package com.tambo.tambo_delivery_backend.auth.controllers;

import com.tambo.tambo_delivery_backend.auth.dto.request.LoginRequest;
import com.tambo.tambo_delivery_backend.auth.dto.request.RegistrationRequest;
import com.tambo.tambo_delivery_backend.auth.dto.response.LoginResponse;
import com.tambo.tambo_delivery_backend.auth.dto.response.UserResponseDto;
import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.services.LoginService;
import com.tambo.tambo_delivery_backend.auth.services.UserService;
import com.tambo.tambo_delivery_backend.auth.services.ResetPasswordService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService registrationService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    ResetPasswordService resetPasswordService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = loginService.loginUser(request);

        HttpStatus status;
        if (loginResponse.getCode() == 200) {
            status = HttpStatus.OK;
        } else if (loginResponse.getCode() == 401) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(loginResponse, status);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegistrationRequest request) {
        UserResponseDto registrationResponse = registrationService.createUser(request);

        return new ResponseEntity<>(registrationResponse,
                registrationResponse.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> map) {
        String userName = map.get("userName");
        String code = map.get("code");

        User user = (User) userDetailsService.loadUserByUsername(userName);
        if (null != user && user.getVerificationCode().equals(code)) {
            registrationService.verifyUser(userName);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        return resetPasswordService.sendResetPasswordEmail(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        return resetPasswordService.resetPassword(token, newPassword);
    }

}
