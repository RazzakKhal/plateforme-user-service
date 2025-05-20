package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto getUser(Authentication authentication) {
        Optional<User> optUser = userRepository.findByMail(((User) authentication.getPrincipal()).getMail());
        if(optUser.isPresent()){
            User user = optUser.get();
            user.getAdress().getAdressLine1();
            return userMapper.userToUserDto(user);

        }else{
            throw new RuntimeException("l'utilisateur ne semble pas exister en BDD");
        }
    }

    public Map<String, String> createUser(SubscriptionDto subscriptionDto) {
        // faire mapping entre Dto et vrai User

        var user = userRepository.save(userMapper.subscriptionDtoToUser(subscriptionDto));
        String token = jwtUtil.generateToken(user);
        return Map.of("token", token, "username", user.getMail(), "roles", user.getAuthorities().toString());

    }

    public Map<String, String> login(LoginDto loginDto) {
        var user = userRepository.findByMail(loginDto.getMail()).orElseThrow(() -> new RuntimeException("Email non existant en BDD"));
        String token = jwtUtil.generateToken(user);
        return Map.of("token", token, "username", user.getMail(), "roles", user.getAuthorities().toString());
    }

    public Map<String, String> validateToken(String token) {
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
