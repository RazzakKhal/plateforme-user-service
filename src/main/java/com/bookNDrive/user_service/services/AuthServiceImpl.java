package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;
import com.bookNDrive.user_service.interfaces.AuthService;
import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ForgotPasswordToken getForgotPasswordTokenFromMail(String mail) {

        User user = userRepository.findByMail(mail).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new ForgotPasswordToken(mail, jwtUtil.generateToken(user));
    }
}
