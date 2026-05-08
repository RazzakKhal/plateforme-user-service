package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.services.UserService;
import com.bookndrive.common.error.ErrorResponseDto;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Expose les operations de consultation et de validation autour des utilisateurs.")
@RefreshScope
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Recuperer l'utilisateur courant",
            description = "Retourne le profil de l'utilisateur authentifie a partir du contexte de securite courant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profil utilisateur retourne avec succes",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication));
    }

    @Operation(
            summary = "Valider un token utilisateur",
            description = "Valide un token d'authentification fourni dans le header Authorization et retourne l'email ainsi que les roles de l'utilisateur."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token valide",
                    content = @Content(schema = @Schema(type = "object"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Token absent, mal forme ou invalide",
                    content = @Content
            )
    })
    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(
            @Parameter(
                    description = "Header Authorization au format `Bearer <jwt>`.",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
            )
            @RequestHeader("Authorization") @NotBlank(message = "must not be blank") String token
    ) {
        return ResponseEntity.ok(userService.validateToken(token));
    }
}
