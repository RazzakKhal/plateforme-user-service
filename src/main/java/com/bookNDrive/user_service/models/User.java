package com.bookNDrive.user_service.models;

import com.bookNDrive.user_service.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
@Schema(
        name = "userDto",
        description = "il faudra un userdto pour requestbody et un autre pour le retour"
)
public class User extends BaseEntity implements UserDetails {

    public User(){
        this.roles.add(Role.ROLE_USER);
    }

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
            name = "phone",
            example = "0668696068"
    )
    private String phone;

    @Schema(
            name = "formulaId",
            example = "123456"
    )
    private Long formulaId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adress_id")
    private Adress adress;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream().map((roleEnum) -> new SimpleGrantedAuthority(roleEnum.name())).toList();
    }

    @Override
    public String getUsername() {
        return this.getMail();
    }

}
