package com.bookNDrive.user_service.dtos.received;

import lombok.Data;

@Data
public class LoginDto {

    private String mail;

    private String password;
}
