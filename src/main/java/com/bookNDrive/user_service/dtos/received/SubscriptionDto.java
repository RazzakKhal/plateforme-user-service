package com.bookNDrive.user_service.dtos.received;

import lombok.Data;

@Data
public class SubscriptionDto {

    private String firstname;
    private String lastname;
    private String mail;
    private String password;
    private AddressDto address;


}
