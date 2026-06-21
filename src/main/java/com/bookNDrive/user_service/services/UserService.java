package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.received.PaymentDto;
import com.bookNDrive.user_service.dtos.received.UserRequest;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.entities.User;
import com.bookNDrive.user_service.events.FormulaSavedEvent;
import com.bookNDrive.user_service.exceptions.EntityNotFoundException;
import com.bookNDrive.user_service.exceptions.ErrorsMessages;
import com.bookNDrive.user_service.exceptions.InvalidTokenException;
import com.bookNDrive.user_service.exceptions.UserErrorCodes;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import com.bookndrive.common.util.SensitiveDataMasker;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final OutboxService outboxService;

    @Autowired
    public UserService(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            UserMapper userMapper,
            OutboxService outboxService
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.outboxService = outboxService;
    }

    @Transactional
    public UserDto getUser(Authentication authentication) {
        String principal = authentication != null ? authentication.getName() : "anonymous";
        log.info("Recuperation du profil courant principal={}", principal);
        var user = userRepository.findByMail(((User) authentication.getPrincipal()).getMail())
                .orElseThrow(() -> {
                    log.warn("Profil courant introuvable principal={}", principal);
                    return new EntityNotFoundException(
                            "Ce mail ne correspond a aucun compte existant",
                            UserErrorCodes.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });
        user.getAddress().getAddressLine1();
        log.info("Profil courant recupere userId={}", user.getId());
        return userMapper.userToUserDto(user);
    }

    @Transactional
    public UserDto updateMe(UserRequest userRequest, Authentication authentication) {
        var authenticatedUser = userRepository.findByMail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException(ErrorsMessages.USER_NOT_FOUND, UserErrorCodes.USER_NOT_FOUND, HttpStatus.NOT_FOUND)
        );
        authenticatedUser.setLastname(userRequest.lastname());
        authenticatedUser.setFirstname(userRequest.firstname());
        authenticatedUser.setPhone(userRequest.phone());

        var userAddress = authenticatedUser.getAddress();
        userAddress.setAddressLine1(userRequest.address().getAddressLine1());
        userAddress.setCity(userRequest.address().getCity());
        userAddress.setCountry(userRequest.address().getCountry());
        userAddress.setPostalCode(userRequest.address().getPostalCode());

        return userMapper.userToUserDto(authenticatedUser);

    }

    public Map<String, String> validateToken(String token) {
        log.info("Validation d'un token demandee");
        try {
            String jwt = token.replace("Bearer", "").trim();
            String mail = jwtUtil.extractUsername(jwt);
            Optional<User> userOptional = userRepository.findByMail(mail);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (jwtUtil.validateToken(jwt, (UserDetails) user)) {
                    log.info("Token valide mail={} roles={}", SensitiveDataMasker.maskEmail(mail), user.getAuthorities());
                    return Map.of("mail", mail, "roles", user.getAuthorities().toString());
                }
            }
        } catch (Exception ex) {
            log.warn("Validation de token echouee, token invalide");
            throw new InvalidTokenException("Le token fourni est invalide", UserErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        log.warn("Validation de token echouee, token invalide");
        throw new InvalidTokenException("Le token fourni est invalide", UserErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public void insertFormulaFromKafka(PaymentDto paymentDto) {
        log.info(
                "Message de paiement recu userId={} formulaId={} status={}",
                paymentDto.getUserId(),
                paymentDto.getFormulaId(),
                paymentDto.getStatus()
        );
        var user = userRepository.findById(paymentDto.getUserId())
                .orElseThrow(() -> {
                    log.warn("Mise a jour de formule impossible, utilisateur introuvable userId={}", paymentDto.getUserId());
                    return new EntityNotFoundException(
                            "Cet id ne correspond a aucun compte existant",
                            UserErrorCodes.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });
        user.setFormulaId(paymentDto.getFormulaId());
        userRepository.save(user);
        log.info("Formule associee a l'utilisateur userId={} formulaId={}", user.getId(), paymentDto.getFormulaId());
    }

    @Transactional
    public void saveFormulaFromKafka(PaymentDto paymentDto) {
        insertFormulaFromKafka(paymentDto);
        try {
            outboxService.saveEventBeforePublishing(new FormulaSavedEvent(paymentDto.getReference()));
            log.info("Evenement FormulaSavedEvent cree reference={}", paymentDto.getReference());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Impossible de serialiser FormulaSavedEvent pour la reference " + paymentDto.getReference(),
                    ex
            );
        }
    }
}
