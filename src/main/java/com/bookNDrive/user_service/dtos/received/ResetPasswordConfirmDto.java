package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordConfirmDto(
        @NotBlank(message = "must not be blank")
        String password,
        @NotBlank(message = "must not be blank")
        String token
) {
}
