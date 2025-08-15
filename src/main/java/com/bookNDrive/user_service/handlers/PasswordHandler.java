package com.bookNDrive.user_service.handlers;

public interface PasswordHandler {

    String encodePassword(String password);

    boolean verifyPassword(String clearPassword, String encodePassword);
}
