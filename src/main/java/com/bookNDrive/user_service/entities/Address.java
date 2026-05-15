package com.bookNDrive.user_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "addresses")
public class Address extends BaseEntity {


    private String addressLine1;
    private String city;
    private String postalCode;
    private String country;
}
