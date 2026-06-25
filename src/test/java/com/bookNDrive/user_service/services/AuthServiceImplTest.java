package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.exceptions.EntityNotFoundException;
import com.bookNDrive.user_service.handlers.PasswordHandler;
import com.bookNDrive.user_service.mappers.UserMapper;
import com.bookNDrive.user_service.repositories.UserRepository;
import com.bookNDrive.user_service.security.JwtUtil;
import com.bookNDrive.user_service.user.UserTestBuilder;
import com.bookndrive.common.util.SensitiveDataMasker;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordHandler passwordHandler;

    @Mock
    private OutboxService outboxService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Logger log;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getForgotPasswordTokenFromMailShouldSuccess() throws JsonProcessingException {

        try (
                var mockSensitive = mockStatic(SensitiveDataMasker.class)
        ) {

            // Arrange
            when(userRepository.findByMail(any()))
                    .thenReturn(Optional.of(
                            UserTestBuilder.aUser().build()
                    ));
            mockSensitive.when(() -> SensitiveDataMasker.maskEmail(any()))
                    .thenReturn("razzak@gmail.comd");

            var mail = "razzak@gmail.com";

            // Act
            authServiceImpl.getForgotPasswordTokenFromMail(mail);

            // Assert

            mockSensitive.verify(() -> SensitiveDataMasker.maskEmail(mail));
            verify(userRepository).findByMail(mail);
            verify(outboxService).saveEventBeforePublishing(any());

        }


    }

    @Test
    void getForgotPasswordTokenFromMailShouldThrowAnException() throws JsonProcessingException {
        try (var sensitiveMaker = mockStatic(SensitiveDataMasker.class)) {
            // Arrange

            when(userRepository.findByMail(any()))
                    .thenReturn(Optional.empty());
            var mail = "test@gmail.com";

            sensitiveMaker.when(() -> SensitiveDataMasker.maskEmail(any()))
                    .thenReturn(mail);

            // Act + Assert
            EntityNotFoundException exception =
                    assertThrows(
                            EntityNotFoundException.class,
                            () -> authServiceImpl.getForgotPasswordTokenFromMail(mail)
                    );
            assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatus());
            assertEquals("Ce mail ne correspond a aucun compte existant", exception.getMessage());
            verify(userRepository).findByMail(mail);
            sensitiveMaker.verify(
                    () -> SensitiveDataMasker.maskEmail(mail)
            );
            verify(outboxService, never()).saveEventBeforePublishing(any());
        }
    }

    @Test
    void resetUserPassword() {
    }

    @Test
    void createUser() {
    }

    @Test
    void login() {
    }
}