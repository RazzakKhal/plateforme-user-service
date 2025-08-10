package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name="User Controller", description = "controller en charge de l'inscription et la connexion")
@RefreshScope
public class UserController {

    private final UserService userService;

    @Value("${test}")
    String testo;

    @Autowired
    public UserController(UserService userService){

        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication));
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
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userService.login(loginDto));
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
    public ResponseEntity<Map<String, String>> createUser(@RequestBody SubscriptionDto subscriptionDto){
        System.out.println("le subscriptiondto : " + subscriptionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(subscriptionDto));
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
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(userService.validateToken(token));
    }

    @PutMapping("/formula")
    public ResponseEntity<Void> updateUserFormula(@RequestParam Long formulaId) {
        userService.updateUserFormula(formulaId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/myenv")
    public String getEnv(){
        return testo;
    }
}
