package com.bookNDrive.user_service.user;

import com.bookNDrive.user_service.BaseEntityTest;
import com.bookNDrive.user_service.entities.Address;
import com.bookNDrive.user_service.entities.User;
import com.bookNDrive.user_service.enums.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserTestBuilder extends BaseEntityTest {


    private String firstname = "Razzak";
    private String lastname = "Khalfallah";

    private String mail = "razzak@gmail.com";
    private String password = "testrazzak";

    private String phone = "0669213535";

    private UUID formulaId = null;

    private Set<Role> roles = new HashSet<>();
    private Address address = null;

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public UserTestBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public UserTestBuilder withMail(String mail) {
        this.mail = mail;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserTestBuilder withFormulaId(UUID formulaId) {
        this.formulaId = formulaId;
        return this;
    }

    public UserTestBuilder withRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserTestBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public User build() {
        return new User(
                firstname,
                lastname,
                mail,
                password,
                phone,
                formulaId,
                roles,
                address
        );
    }
}
