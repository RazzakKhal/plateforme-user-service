package com.bookNDrive.user_service.repositories;

import com.bookNDrive.user_service.models.Adress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdressRepository extends JpaRepository<Adress, Long> {
}
