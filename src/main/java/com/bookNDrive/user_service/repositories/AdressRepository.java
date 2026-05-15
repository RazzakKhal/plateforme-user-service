package com.bookNDrive.user_service.repositories;

import com.bookNDrive.user_service.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdressRepository extends JpaRepository<Address, UUID> {
}
