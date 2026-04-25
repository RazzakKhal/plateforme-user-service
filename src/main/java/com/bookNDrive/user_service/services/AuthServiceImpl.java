package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;
import com.bookNDrive.user_service.dtos.sended.TokenDto;
import com.bookNDrive.user_service.entities.User;
import com.bookNDrive.user_service.events.ForgotPasswordTokenCreated;
import com.bookNDrive.user_service.exceptions.EntityNotFoundException;
import com.bookNDrive.user_service.exceptions.ExistingEntityException;
import com.bookNDrive.user_service.exceptions.WrongPasswordException;
import com.bookNDrive.user_service.handlers.PasswordHandler;
import com.bookNDrive.user_service.interfaces.AuthService;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordHandler passwordHandler;
    private final OutboxService outboxService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, UserMapper userMapper, PasswordHandler passwordHandler, OutboxService outboxService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordHandler = passwordHandler;
        this.outboxService = outboxService;
    }


    @Override
    @Transactional
    public void getForgotPasswordTokenFromMail(String mail) throws JsonProcessingException {

        User user = userRepository.findByMail(mail).orElseThrow(() -> new EntityNotFoundException("Ce mail ne correspond à aucun compte existant", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        var token = new ForgotPasswordToken(mail, jwtUtil.generateToken(user));
        outboxService.saveEventBeforePublishing(new ForgotPasswordTokenCreated(token));
    }

    @Override
    @Transactional
    public TokenDto resetUserPassword(ResetPasswordConfirmDto resetPasswordConfirmDto) {

        var user = userRepository.findByMail(
                jwtUtil.extractUsername(resetPasswordConfirmDto.token())
        ).orElseThrow(() -> new EntityNotFoundException("Ce mail ne correspond à aucun compte existant", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        user.setPassword(passwordHandler.encodePassword(resetPasswordConfirmDto.password()));
        userRepository.save(user);

        return new TokenDto(user.getMail(), user.getAuthorities().toString(), jwtUtil.generateToken(user));

    }

    @Override
    @Transactional
    public TokenDto createUser(SubscriptionDto subscriptionDto) {

        userRepository.findByMail(subscriptionDto.getMail())
                .ifPresent(user -> {
                    throw new ExistingEntityException("l'utilisateur existe déjà en base", "USER_ALREADY_EXIST", HttpStatus.BAD_REQUEST);
                });

        subscriptionDto.setPassword(passwordHandler.encodePassword(subscriptionDto.getPassword()));
        var user = userRepository.save(userMapper.subscriptionDtoToUser(subscriptionDto));
        String token = jwtUtil.generateToken(user);
        return new TokenDto(user.getMail(), user.getAuthorities().toString(), token);

    }

    @Override
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        var user = userRepository.findByMail(loginDto.getMail()).orElseThrow(() -> new EntityNotFoundException("Ce mail ne correspond à aucun compte existant", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!passwordHandler.verifyPassword(loginDto.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Connexion impossible, veuillez vérifier vos informations de connexion et réessayer", "WRONG_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(user);
        return new TokenDto(user.getMail(), user.getAuthorities().toString(), token);
    }


}
