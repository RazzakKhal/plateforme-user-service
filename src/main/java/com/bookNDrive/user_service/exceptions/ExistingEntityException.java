package com.bookNDrive.user_service.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@Data
public class ExistingEntityException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public ExistingEntityException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
