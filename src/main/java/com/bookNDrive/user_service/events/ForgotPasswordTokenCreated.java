package com.bookNDrive.user_service.events;

import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;

public record ForgotPasswordTokenCreated(ForgotPasswordToken token) implements Event {

}
