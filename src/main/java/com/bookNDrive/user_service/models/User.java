package com.bookNDrive.user_service.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
@Schema(
        name = "userDto",
        description = "il faudra un userdto pour requestbody et un autre pour le retour"
)
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

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

    @Schema(
            name = "formulaId",
            example = "123456"
    )
    private Long formulaId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.getMail();
    }

}
