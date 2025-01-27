package com.bookNDrive.user_service.models;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
