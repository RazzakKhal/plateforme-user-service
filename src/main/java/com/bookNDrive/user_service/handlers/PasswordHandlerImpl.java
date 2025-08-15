package com.bookNDrive.user_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHandlerImpl implements PasswordHandler{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordHandlerImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String clearPassword, String encodePassword) {
        return bCryptPasswordEncoder.matches(clearPassword, encodePassword);
    }
}
