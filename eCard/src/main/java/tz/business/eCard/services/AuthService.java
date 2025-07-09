package tz.business.eCard.services;

import tz.business.eCard.dtos.ChangePasswordDto;
import tz.business.eCard.dtos.LoginDto;
import tz.business.eCard.dtos.LoginResponseDto;
import tz.business.eCard.dtos.AccountDto;
import tz.business.eCard.models.Account;
import tz.business.eCard.utils.Response;

public interface AuthService {
    Response<LoginResponseDto> login(LoginDto loginDto);
    Response<?> register(AccountDto accountDto);
    Response<LoginResponseDto> revokeToken(String refreshToken);
    Response<Boolean> forgotPassword(String email);
    Response<Account> getLoggedUser();
    Response<Account> updatePassword(ChangePasswordDto changePasswordDto);
    Response<String> activateAccount(String code);
    Response<String> requestOTP(String phoneNumber);
    Response<Account> loginByEmail(String email);
    Response<LoginResponseDto> generateNewAccessToken(String refreshToken , LoginDto loginDto);

}
