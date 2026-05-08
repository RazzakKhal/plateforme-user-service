package com.bookNDrive.user_service.exceptions;

public final class UserErrorCodes {

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";
    public static final String WRONG_CREDENTIALS = "WRONG_CREDENTIALS";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String INVALID_RESET_PASSWORD_TOKEN = "INVALID_RESET_PASSWORD_TOKEN";

    private UserErrorCodes() {
    }
}
