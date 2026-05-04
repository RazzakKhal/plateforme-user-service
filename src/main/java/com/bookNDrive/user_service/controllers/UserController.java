package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "controller en charge de l'inscription et la connexion")
@RefreshScope
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication));
    }


    @Operation(
            summary = "Valide le token d'un utilisateur",
            description = "Valide un token d'authentification fourni dans le header Authorization"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Utilisateur correctement authentifié"
    )
    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.validateToken(token));
    }


}
