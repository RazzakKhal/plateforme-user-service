package com.bookNDrive.user_service.dtos.received;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "must not be blank")
    private String adressLine1;
    @NotBlank(message = "must not be blank")
    private String city;
    @NotBlank(message = "must not be blank")
    private String postalCode;
    @NotBlank(message = "must not be blank")
    private String country;
}
