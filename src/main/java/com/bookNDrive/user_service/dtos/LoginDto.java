package com.bookNDrive.user_service.dtos;

import lombok.Data;

@Data
public class LoginDto {

    private String mail;

    private String password;
}
