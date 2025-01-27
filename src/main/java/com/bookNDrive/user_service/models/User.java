package com.bookNDrive.user_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
public class User extends BaseEntity{

    private String firstname;

    private String lastname;

    @Column(unique = true)
    private String mail;

    private String password;
}
