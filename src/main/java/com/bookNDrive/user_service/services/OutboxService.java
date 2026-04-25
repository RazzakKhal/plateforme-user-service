package com.bookNDrive.user_service.services;

import com.bookNDrive.user_service.entities.Outbox;
import com.bookNDrive.user_service.enums.EventDestination;
import com.bookNDrive.user_service.enums.EventPublishStatus;
import com.bookNDrive.user_service.events.Event;
import com.bookNDrive.user_service.events.ForgotPasswordTokenCreated;
import com.bookNDrive.user_service.exceptions.ErrorsMessages;
import com.bookNDrive.user_service.repositories.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;

    public <T extends Event> void saveEventBeforePublishing(T event) throws JsonProcessingException {
        var outbox = new Outbox();
        outbox.setStatus(EventPublishStatus.PENDING);
        outbox.setEventName(event.getClass().getName());
        outbox.setPayload(objectMapper.writeValueAsString(event));
        outboxRepository.save(outbox);
    }


    @Transactional
    public void processPendingEvents() {
        var pendingEvents = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(EventPublishStatus.PENDING);

        for (Outbox outbox : pendingEvents) {
            try {
                boolean sent = publish(outbox);

                if (sent) {
                    outbox.setStatus(EventPublishStatus.PUBLISHED);
                    outbox.setPublishedAt(LocalDateTime.now());
                } else {
                    markAsFailed(outbox, ErrorsMessages.KAFKA_SEND_ERROR);
                }

            } catch (Exception ex) {
                markAsFailed(outbox, ex.getMessage());
            }

            outboxRepository.save(outbox);
        }
    }

    private boolean publish(Outbox outbox) throws JsonProcessingException {

        if (ForgotPasswordTokenCreated.class.getName().equals(outbox.getEventName())) {
            var event = objectMapper.readValue(
                    outbox.getPayload(),
                    ForgotPasswordTokenCreated.class
            );

            return kafkaService.sendMessage(
                    EventDestination.SEND_FORGOT_PASSWORD_LINK.getDestination(),
                    event.token()
            );
        }

        throw new IllegalArgumentException(
                "Unsupported event type: " + outbox.getEventName()
        );
    }

    private void markAsFailed(Outbox outbox, String errorMessage) {
        outbox.setStatus(EventPublishStatus.FAILED);
        outbox.setRetryCount(outbox.getRetryCount() + 1);
        outbox.setLastError(errorMessage);
    }
}
