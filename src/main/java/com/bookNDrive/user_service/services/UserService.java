package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.dtos.received.PaymentDto;
import com.bookNDrive.user_service.dtos.sended.UserDto;
import com.bookNDrive.user_service.entities.User;
import com.bookNDrive.user_service.exceptions.EntityNotFoundException;
import com.bookNDrive.user_service.exceptions.InvalidTokenException;
import com.bookNDrive.user_service.exceptions.UserErrorCodes;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        var user = userRepository.findByMail(((User) authentication.getPrincipal()).getMail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ce mail ne correspond à aucun compte existant",
                        UserErrorCodes.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));
        user.getAdress().getAdressLine1();
        return userMapper.userToUserDto(user);
    }

    public Map<String, String> validateToken(String token) {
        try {
            String jwt = token.replace("Bearer", "").trim();
            String mail = jwtUtil.extractUsername(jwt);
            Optional<User> userOptional = userRepository.findByMail(mail);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (jwtUtil.validateToken(jwt, (UserDetails) user)) {
                    return Map.of("mail", mail, "roles", user.getAuthorities().toString());
                }
            }
        } catch (Exception ex) {
            throw new InvalidTokenException("Le token fourni est invalide", UserErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        throw new InvalidTokenException("Le token fourni est invalide", UserErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public void insertFormulaFromKafka(PaymentDto paymentDto) {
        var user = userRepository.findById(paymentDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cet id ne correspond à aucun compte existant",
                        UserErrorCodes.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));
        user.setFormulaId(paymentDto.getFormulaId());
        userRepository.save(user);
    }
}
