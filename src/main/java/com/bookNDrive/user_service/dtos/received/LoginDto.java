package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    private String mail;

    @NotBlank(message = "must not be blank")
    private String password;
}
