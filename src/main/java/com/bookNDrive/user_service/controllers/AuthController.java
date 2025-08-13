package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;
import com.bookNDrive.user_service.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<Void> getForgotPasswordLink(@RequestParam("mail") String mail){
        authService.getForgotPasswordTokenFromMail(mail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
