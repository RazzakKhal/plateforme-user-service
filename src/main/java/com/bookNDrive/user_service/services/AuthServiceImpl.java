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
import com.bookNDrive.user_service.exceptions.InvalidTokenException;
import com.bookNDrive.user_service.exceptions.UserErrorCodes;
import com.bookNDrive.user_service.exceptions.WrongPasswordException;
import com.bookNDrive.user_service.handlers.PasswordHandler;
import com.bookNDrive.user_service.interfaces.AuthService;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import com.bookndrive.common.util.SensitiveDataMasker;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordHandler passwordHandler;
    private final OutboxService outboxService;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            UserMapper userMapper,
            PasswordHandler passwordHandler,
            OutboxService outboxService
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordHandler = passwordHandler;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
    public void getForgotPasswordTokenFromMail(String mail) throws JsonProcessingException {
        String maskedMail = SensitiveDataMasker.maskEmail(mail);
        log.info("Demande de lien de reinitialisation recue mail={}", maskedMail);
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> {
                    log.warn("Demande de lien de reinitialisation impossible, utilisateur introuvable mail={}", maskedMail);
                    return new EntityNotFoundException(
                            "Ce mail ne correspond a aucun compte existant",
                            UserErrorCodes.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });
        var token = new ForgotPasswordToken(mail, jwtUtil.generateToken(user));
        outboxService.saveEventBeforePublishing(new ForgotPasswordTokenCreated(token));
        log.info("Evenement de reinitialisation cree mail={}", maskedMail);
    }

    @Override
    @Transactional
    public TokenDto resetUserPassword(ResetPasswordConfirmDto resetPasswordConfirmDto) {
        log.info("Demande de reinitialisation de mot de passe recue");
        String mailFromToken;
        try {
            mailFromToken = jwtUtil.extractUsername(resetPasswordConfirmDto.token());
        } catch (Exception ex) {
            log.warn("Echec de reinitialisation, token invalide");
            throw new InvalidTokenException(
                    "Le token de reinitialisation est invalide",
                    UserErrorCodes.INVALID_RESET_PASSWORD_TOKEN,
                    HttpStatus.UNAUTHORIZED
            );
        }

        var user = userRepository.findByMail(mailFromToken)
                .orElseThrow(() -> {
                    log.warn(
                            "Echec de reinitialisation, utilisateur introuvable mail={}",
                            SensitiveDataMasker.maskEmail(mailFromToken)
                    );
                    return new EntityNotFoundException(
                            "Ce mail ne correspond a aucun compte existant",
                            UserErrorCodes.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });

        user.setPassword(passwordHandler.encodePassword(resetPasswordConfirmDto.password()));
        userRepository.save(user);
        log.info("Mot de passe reinitialise mail={}", SensitiveDataMasker.maskEmail(user.getMail()));

        return new TokenDto(user.getMail(), user.getAuthorities().toString(), jwtUtil.generateToken(user));
    }

    @Override
    @Transactional
    public TokenDto createUser(SubscriptionDto subscriptionDto) {
        String maskedMail = SensitiveDataMasker.maskEmail(subscriptionDto.getMail());
        log.info("Creation de compte demandee mail={}", maskedMail);
        userRepository.findByMail(subscriptionDto.getMail())
                .ifPresent(user -> {
                    log.warn("Creation de compte refusee, utilisateur deja existant mail={}", maskedMail);
                    throw new ExistingEntityException(
                            "L'utilisateur existe deja en base",
                            UserErrorCodes.USER_ALREADY_EXIST,
                            HttpStatus.BAD_REQUEST
                    );
                });

        subscriptionDto.setPassword(passwordHandler.encodePassword(subscriptionDto.getPassword()));
        var user = userRepository.save(userMapper.subscriptionDtoToUser(subscriptionDto));
        String token = jwtUtil.generateToken(user);
        log.info("Compte cree userId={} mail={}", user.getId(), maskedMail);
        return new TokenDto(user.getMail(), user.getAuthorities().toString(), token);
    }

    @Override
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        String maskedMail = SensitiveDataMasker.maskEmail(loginDto.getMail());
        log.info("Tentative de connexion mail={}", maskedMail);
        var user = userRepository.findByMail(loginDto.getMail())
                .orElseThrow(() -> {
                    log.warn("Connexion refusee, utilisateur introuvable mail={}", maskedMail);
                    return new EntityNotFoundException(
                            "Ce mail ne correspond a aucun compte existant",
                            UserErrorCodes.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });
        if (!passwordHandler.verifyPassword(loginDto.getPassword(), user.getPassword())) {
            log.warn("Connexion refusee, mot de passe invalide mail={}", maskedMail);
            throw new WrongPasswordException(
                    "Connexion impossible, veuillez verifier vos informations de connexion et reessayer",
                    UserErrorCodes.WRONG_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED
            );
        }
        String token = jwtUtil.generateToken(user);
        log.info("Connexion reussie userId={} mail={}", user.getId(), maskedMail);
        return new TokenDto(user.getMail(), user.getAuthorities().toString(), token);
    }
}
