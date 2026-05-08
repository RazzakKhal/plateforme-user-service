package com.bookNDrive.user_service.exceptions;

import com.bookndrive.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApiException {

    public EntityNotFoundException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status.value());
    }
}
