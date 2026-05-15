package com.bookNDrive.user_service.dtos.received;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentDto {

    private String status;

    private UUID userId;

    private UUID formulaId;
}
