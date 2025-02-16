package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.dtos.LoginDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<User> getUser(){
        return ResponseEntity.ok(userService.getUser());
    }

    @Operation(
            summary = "Connexion d'un utilisateur",
            description = "Connexion d'un utilisateur à partir de ses informations de connexion"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Utilisateur correctement connecté"
    )
    @PostMapping("signin")
    public ResponseEntity<User> login(@RequestBody LoginDto loginDto){
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
    public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @GetMapping("/myenv")
    public String getEnv(){
        return testo;
    }
}
