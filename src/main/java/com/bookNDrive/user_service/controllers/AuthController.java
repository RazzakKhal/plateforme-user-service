package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordMailDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.TokenDto;
import com.bookNDrive.user_service.interfaces.AuthService;
import com.bookndrive.common.error.ErrorResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@Tag(name = "Auth Controller", description = "Expose les endpoints d'authentification et de gestion du mot de passe.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Connecter un utilisateur",
            description = "Authentifie un utilisateur a partir de son email et de son mot de passe, puis retourne un JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur authentifie avec succes",
                    content = @Content(schema = @Schema(implementation = TokenDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Identifiants invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> login(

            @RequestBody LoginDto loginDto
    ) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @Operation(
            summary = "Creer un utilisateur",
            description = "Cree un nouveau compte utilisateur et retourne un JWT pour la session ouverte."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur cree avec succes",
                    content = @Content(schema = @Schema(implementation = TokenDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Utilisateur deja existant ou requete invalide",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<TokenDto> createUser(

            @RequestBody SubscriptionDto subscriptionDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(subscriptionDto));
    }

    @Operation(
            summary = "Demander un lien de reinitialisation",
            description = "Genere un token de reinitialisation de mot de passe puis publie une demande d'envoi d'email."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Demande de reinitialisation acceptee"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur associe a cette adresse email",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getForgotPasswordLink(

            @RequestBody ResetPasswordMailDto resetPasswordMailDto
    ) throws JsonProcessingException {
        authService.getForgotPasswordTokenFromMail(resetPasswordMailDto.mail());
    }

    @Operation(
            summary = "Reinitialiser le mot de passe",
            description = "Verifie le token de reinitialisation, remplace le mot de passe et retourne un nouveau JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mot de passe reinitialise avec succes",
                    content = @Content(schema = @Schema(implementation = TokenDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable a partir du token fourni",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<TokenDto> resetUserPassword(@RequestBody ResetPasswordConfirmDto resetPasswordConfirmDto
    ) {
        return ResponseEntity.ok(authService.resetUserPassword(resetPasswordConfirmDto));
    }
}
