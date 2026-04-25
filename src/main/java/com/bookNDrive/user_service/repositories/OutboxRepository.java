package com.bookNDrive.user_service.repositories;

import com.bookNDrive.user_service.entities.Outbox;
import com.bookNDrive.user_service.enums.EventPublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    List<Outbox> findTop50ByStatusOrderByCreatedAtAsc(EventPublishStatus status);
}
