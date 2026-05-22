package com.bookNDrive.user_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHandlerImpl implements PasswordHandler {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordHandlerImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String clearPassword, String encodePassword) {
        return passwordEncoder.matches(clearPassword, encodePassword);
    }
}
