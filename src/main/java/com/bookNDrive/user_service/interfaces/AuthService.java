package com.bookNDrive.user_service.interfaces;


import com.bookNDrive.user_service.dtos.received.LoginDto;
import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.received.SubscriptionDto;
import com.bookNDrive.user_service.dtos.sended.TokenDto;

import java.util.Map;

public interface AuthService {

   void getForgotPasswordTokenFromMail(String mail);

   TokenDto resetUserPassword(ResetPasswordConfirmDto resetPasswordConfirmDto);

   TokenDto createUser(SubscriptionDto subscriptionDto);

   TokenDto login(LoginDto loginDto);
}
