package tz.business.eCard.services;

import org.apache.catalina.User;
import tz.business.eCard.dtos.ChangePasswordDto;
import tz.business.eCard.dtos.LoginDto;
import tz.business.eCard.dtos.LoginResponseDto;
import tz.business.eCard.dtos.UserAccountDto;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.utils.Response;

public interface AuthService {
    Response<LoginResponseDto> login(LoginDto loginDto);
    Response<?> register(UserAccountDto userAccountDto);
    Response<LoginResponseDto> revokeToken(String refreshToken);
    Response<Boolean> forgotPassword(String email);
    Response<UserAccount> getLoggedUser();
    Response<UserAccount> updatePassword(ChangePasswordDto changePasswordDto);
    Response<String> activateAccount(String code);
    Response<String> requestOTP(String phoneNumber);
    Response<UserAccount> loginByEmail(String email);
    Response<LoginResponseDto> generateNewAccessToken(String refreshToken , LoginDto loginDto);

}
