package com.bookNDrive.user_service.services;

import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaService {

    private final StreamBridge streamBridge;

    public <T> boolean sendMessage(String output, T dto) {
        return streamBridge.send(output, dto);
    }
}