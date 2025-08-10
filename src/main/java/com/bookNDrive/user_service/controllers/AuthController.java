package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;
import com.bookNDrive.user_service.interfaces.AuthService;
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
    public ResponseEntity<ForgotPasswordToken> getForgotPasswordLink(@RequestParam("mail") String mail){
        return ResponseEntity.ok(authService.getForgotPasswordTokenFromMail(mail));
    }

}
