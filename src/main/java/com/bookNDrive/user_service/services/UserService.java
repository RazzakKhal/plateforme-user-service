package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.LoginDto;
import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User getUser() {
        var user = userRepository.findByMail("test@gmail.com").orElseThrow();
        return user;
    }

    public User createUser(User user) {
        // faire mapping entre Dto et vrai User

        return userRepository.save(user);

    }

    public Map<String, String> login(LoginDto loginDto) {
        var user = userRepository.findByMail(loginDto.getMail()).orElseThrow(() -> new RuntimeException("Email non existant en BDD"));
        String token = jwtUtil.generateToken(user);
        return Map.of("token", token, "username", user.getMail(), "roles", user.getAuthorities().toString());
    }

    public Map<String, String> validateToken(String token) {
        System.out.println(token);
        String jwt = token.replace("Bearer", "").trim();
        String mail = jwtUtil.extractUsername(jwt);
        Optional<User> userOptional = userRepository.findByMail(mail);

        //  Si le authServer nous réponds en retournant l'utilisateur alors on l'enregistre dans le context
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (jwtUtil.validateToken(jwt, (UserDetails) user)) {
               return Map.of("mail", mail, "roles", user.getAuthorities().toString());
            }
        }

        throw new RuntimeException("token is not valid");
    }
}
