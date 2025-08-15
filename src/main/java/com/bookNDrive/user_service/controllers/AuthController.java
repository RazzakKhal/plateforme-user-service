package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordMailDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.TokenDto;
import com.bookNDrive.user_service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @Operation(
            summary = "Connexion d'un utilisateur",
            description = "Connexion d'un utilisateur à partir de ses informations de connexion"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Utilisateur correctement connecté"
    )
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @Operation(
            summary = "Création d'un utilisateur",
            description = "Création d'un utilisateur qu'on aura fourni dans le body"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Utilisateur correctement créé"
    )
    @PostMapping("/signup")
    public ResponseEntity<TokenDto> createUser(@RequestBody SubscriptionDto subscriptionDto){
        System.out.println("le subscriptiondto : " + subscriptionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(subscriptionDto));
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getForgotPasswordLink(@RequestBody ResetPasswordMailDto resetPasswordMailDto){
        authService.getForgotPasswordTokenFromMail(resetPasswordMailDto.mail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<TokenDto> resetUserPassword(@RequestBody ResetPasswordConfirmDto resetPasswordConfirmDto){
        return ResponseEntity.ok(authService.resetUserPassword(resetPasswordConfirmDto));
    }


}
