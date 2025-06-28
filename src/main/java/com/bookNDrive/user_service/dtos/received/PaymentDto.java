package com.bookNDrive.user_service.dtos.received;

import lombok.Data;

@Data
public class PaymentDto {

    private String status;

    private Long userId;

    private Long formulaId;
}
