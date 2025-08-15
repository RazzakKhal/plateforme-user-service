package com.bookNDrive.user_service.interfaces;


import com.bookNDrive.user_service.dtos.received.ResetPasswordConfirmDto;
import com.bookNDrive.user_service.dtos.sended.TokenDto;

public interface AuthService {

   void getForgotPasswordTokenFromMail(String mail);

   TokenDto resetUserPassword(ResetPasswordConfirmDto resetPasswordConfirmDto);
}
