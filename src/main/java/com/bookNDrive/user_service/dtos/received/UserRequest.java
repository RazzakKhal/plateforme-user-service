package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        @NotBlank
        String phone,

        @Valid
        @NotNull
        AddressDto address
) {
}
