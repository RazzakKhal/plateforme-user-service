package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordMailDto(
        @NotBlank(message = "must not be blank")
        @Email(message = "must be a valid email")
        String mail
) {
}
