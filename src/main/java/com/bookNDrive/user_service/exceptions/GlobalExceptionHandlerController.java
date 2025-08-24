package com.bookNDrive.user_service.exceptions;


import com.bookNDrive.user_service.dtos.sended.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandlerController {

    @ExceptionHandler(ExistingEntityException.class)
    public ResponseEntity<ErrorResponseDto> handleExistingEntityException(ExistingEntityException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getStatus().value()
        );

        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getStatus().value()
        );

        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorResponseDto> wrongPasswordException(WrongPasswordException ex, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getStatus().value()
        );

        return new ResponseEntity<>(error, ex.getStatus());
    }


}