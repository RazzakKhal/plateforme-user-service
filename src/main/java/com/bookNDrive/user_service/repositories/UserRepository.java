package com.bookNDrive.user_service.repositories;

import com.bookNDrive.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMail(String mail);
}
