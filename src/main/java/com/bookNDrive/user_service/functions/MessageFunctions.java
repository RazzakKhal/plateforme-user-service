package com.bookNDrive.user_service.functions;

import com.bookNDrive.user_service.dtos.received.PaymentDto;
import com.bookNDrive.user_service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class MessageFunctions {

    @Bean
    public Consumer<PaymentDto> saveFormula(UserService userService) {
        return paymentDto -> {
            log.info(
                    "Message Kafka recu pour l'association formule userId={} formulaId={} status={}",
                    paymentDto.getUserId(),
                    paymentDto.getFormulaId(),
                    paymentDto.getStatus()
            );
            userService.insertFormulaFromKafka(paymentDto);
        };
    }
}
