package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionDto {

    @NotBlank(message = "must not be blank")
    private String firstname;
    @NotBlank(message = "must not be blank")
    private String lastname;
    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    private String mail;
    @NotBlank(message = "must not be blank")
    private String password;
    @NotBlank(message = "must not be blank")
    private String phone;
    @Valid
    @NotNull(message = "must not be null")
    private AddressDto address;


}
