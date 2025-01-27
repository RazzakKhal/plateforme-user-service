package com.bookNDrive.user_service.controllers;

import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser(){
        return ResponseEntity.ok(userService.getUser());
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(){
        return ResponseEntity.ok(userService.createUser());
    }
}
