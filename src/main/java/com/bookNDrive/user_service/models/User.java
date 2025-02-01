package com.bookNDrive.user_service.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
@Schema(
        name = "userDto",
        description = "il faudra un userdto pour requestbody et un autre pour le retour"
)
public class User extends BaseEntity{

    @Schema(
            name = "firstname",
            example = "razzak"
    )
    private String firstname;

    @Schema(
            name = "lastname",
            example = "khalfallah"
    )
    private String lastname;

    @Schema(
            name = "mail",
            example = "razzak@gmail.com"
    )
    @Column(unique = true)
    private String mail;

    @Schema(
            name = "password",
            example = "razztiti20"
    )
    private String password;
}
