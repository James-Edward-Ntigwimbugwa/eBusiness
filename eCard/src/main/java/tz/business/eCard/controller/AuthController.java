package tz.business.eCard.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.AccountDto;
import tz.business.eCard.dtos.ChangePasswordDto;
import tz.business.eCard.dtos.LoginDto;
import tz.business.eCard.dtos.LoginResponseDto;
import tz.business.eCard.models.Account;
import tz.business.eCard.services.AuthService;
import tz.business.eCard.utils.Response;

import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/auth")
public class
AuthController {
    @Autowired
    private  AuthService authService;

    private Logger logger = Logger.getLogger(String.valueOf(AuthController.class));
    @PostMapping(path = "/login" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        Response<LoginResponseDto> response = authService.login(loginDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register (@RequestBody AccountDto accountDto) {
        Response<?> stringResponse = authService.register(accountDto);
        return ResponseEntity.ok().body(stringResponse);
    }

    @GetMapping(path="/me")
    public ResponseEntity<?> getProfile(){
        Response<Account> response = authService.getLoggedUser();
        return  ResponseEntity.ok().body(response);
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Response<Account> response = authService.updatePassword(changePasswordDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam String otp) {
        Response<?> response = authService.activateAccount(otp);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/revoke-token")
    public  ResponseEntity<?> revokeToken(@RequestParam String refreshToken){
        Response<LoginResponseDto> response = authService.revokeToken(refreshToken);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/generate-new-access-token")
    public ResponseEntity<?> generateNewAccessToken(
            @NotNull(message = "refreshToken is required")
            @RequestParam String refreshToken ,
            @RequestBody LoginDto loginResponseDto)
    {
        Response<LoginResponseDto> response = authService.generateNewAccessToken(refreshToken , loginResponseDto);
        return ResponseEntity.ok().body(response);
    }
}
