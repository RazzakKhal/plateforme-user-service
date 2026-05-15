package com.bookNDrive.user_service.dtos.sended;

import com.bookNDrive.user_service.enums.Role;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String firstname;
    private String lastname;
    private String mail;
    private String phone;
    private UUID formulaId;
    private Set<Role> roles;
    private AddressDto address;
}
