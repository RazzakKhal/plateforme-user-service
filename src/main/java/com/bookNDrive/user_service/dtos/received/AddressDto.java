package com.bookNDrive.user_service.dtos.received;

import lombok.Data;

@Data
public class AddressDto {
    private String adressLine1;
    private String city;
    private String postalCode;
    private String country;
}
