package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordMailDto;
import com.bookNDrive.user_service.dtos.sended.TokenDto;
import com.bookNDrive.user_service.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getForgotPasswordLink(@RequestBody ResetPasswordMailDto resetPasswordMailDto){
        authService.getForgotPasswordTokenFromMail(resetPasswordMailDto.mail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<TokenDto> updateUser(@RequestBody ResetPasswordConfirmDto resetPasswordConfirmDto){
        return ResponseEntity.ok(authService.resetUserPassword(resetPasswordConfirmDto));
    }


}
