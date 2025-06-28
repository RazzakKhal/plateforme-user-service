package com.bookNDrive.user_service.functions;

import com.bookNDrive.user_service.dtos.received.PaymentDto;
import com.bookNDrive.user_service.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageFunctions {

    @Bean
    public Consumer<PaymentDto> saveFormula(UserService userService){
        return userService::insertFormulaFromKafka;
    }
}