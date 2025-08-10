package com.bookNDrive.user_service.interfaces;

import com.bookNDrive.user_service.dtos.sended.ForgotPasswordToken;

public interface AuthService {

   ForgotPasswordToken getForgotPasswordTokenFromMail(String mail);
}
