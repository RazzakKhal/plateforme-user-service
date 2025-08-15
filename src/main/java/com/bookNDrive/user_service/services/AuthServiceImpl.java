package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;
import com.bookNDrive.user_service.dtos.sended.TokenDto;
import com.bookNDrive.user_service.interfaces.AuthService;
import com.bookNDrive.user_service.models.User;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final KafkaService kafkaService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, KafkaService kafkaService){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.kafkaService = kafkaService;
    }


    @Override
    public void getForgotPasswordTokenFromMail(String mail) {

        User user = userRepository.findByMail(mail).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        var token =  new ForgotPasswordToken(mail, jwtUtil.generateToken(user));
        System.out.println("le token : " + token);
        kafkaService.sendMessage("send-forgot-password-link-out-0", token);
    }

    @Override
    @Transactional
    public TokenDto resetUserPassword(ResetPasswordConfirmDto resetPasswordConfirmDto){

        var user = userRepository.findByMail(
                jwtUtil.extractUsername(resetPasswordConfirmDto.token())
        ).orElseThrow(() -> new RuntimeException("utilisateur non trouvé lors de la réinitialisation"));

        user.setPassword(resetPasswordConfirmDto.password());
        userRepository.save(user);

        return new TokenDto(jwtUtil.generateToken(user));

    }
}
