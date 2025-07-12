package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.business.eCard.dtos.*;
import tz.business.eCard.jwt.JWTutils;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.AccountRepository;
import tz.business.eCard.services.AuthService;
import tz.business.eCard.services.BulkSmsIntegration;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.UserType;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder ;
    @Autowired
    private JWTutils jwtUtils ;
    @Autowired
    private BulkSmsIntegration bulkSmsIntegration;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoggedUser loggedUser;

    @Override
    public Response<LoginResponseDto> login(LoginDto loginDto) {
        try {
            log.info("Attempting authentication for username: {}", loginDto.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = UUID.randomUUID().toString();

            Optional<Account> accountOptional = accountRepository.findFirstByUserName(loginDto.getUsername());

            if(accountOptional.isEmpty())
                return new Response<>(true, ResponseCode.NOT_FOUND,"Invalid login credentials");

            Account account = accountOptional.get();
            if(!account.getActive()) {
                log.info("ACCOUNT NOT ACTIVATED");
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Please activate your account first");
            }
            else {
                return this.getLoginResponse(accountOptional, jwtToken, refreshToken);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.NOT_FOUND,"Invalid login credentials");
    }

    @Override
    public Response<Account> register(AccountDto accountDto) {
        try {
            if(accountDto.getFirstName() == null || accountDto.getLastName()==null){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Invalid first name or last name");
            }

            if(accountDto.getFirstName().isEmpty() || accountDto.getLastName().isEmpty()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "First name and last name are required");
            }

            if(accountDto.getPassword().isBlank()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Password is required");
            }

            if(accountDto.getPhoneNumber()== null || accountDto.getPhoneNumber().isEmpty()){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT, "Phone number is required");
            }

            if(!isValidEmail(accountDto.getEmail())){
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid email");
            }
            if(isValidPhoneNumber(accountDto.getPhoneNumber())){
                return new Response<>(true , ResponseCode.BAD_REQUEST,"Invalid phone number");
            }

            if(accountDto.getJobTitle()== null || accountDto.getJobTitle().isEmpty()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Job title is required");
            }

            if(accountDto.getCompanyTitle()==null|| accountDto.getCompanyTitle().isEmpty()){
                return  new Response<>(true, ResponseCode.NULL_ARGUMENT, "Company title is required");
            }

            Optional<Account> phoneNumber = accountRepository.findFirstByPhoneNumber(accountDto.getPhoneNumber());
            if(phoneNumber.isPresent()){
                return  new Response<>(true,ResponseCode.DUPLICATE_KEY , "Account with this phone number already exist");
            }
            Optional<Account> email = accountRepository.findFirstByEmail(accountDto.getEmail());
            if(email.isPresent()){
                return new Response<>(true, ResponseCode.DUPLICATE_KEY, "Account with this email already exist");
            }

            Optional<Account> username = accountRepository.findFirstByUserName(accountDto.getUsername());
            if (username.isPresent()){
                return new Response<>(true, ResponseCode.DUPLICATE_KEY, "Account with this username already exist");
            }

            Account account = new Account();
            Random rand = new SecureRandom();
            int nextInt = rand.nextInt(100001 , 999999);
            if(accountDto.getUserRole()==null || accountDto.getUserRole().isEmpty()){
                account.setUserType(UserType.CUSTOMER.toString());
            }
            else{
                account.setUserType(accountDto.getUserRole());
            }

            account.setEnabled(false);
            account.getUuid();
            account.setFirstName(accountDto.getFirstName());
            account.setLastName(accountDto.getLastName());
            account.setFullName(accountDto.getFirstName() + " " + accountDto.getMiddleName() +" " + accountDto.getLastName());
            account.setPhoneNumber(accountDto.getPhoneNumber());
            account.setUserName(accountDto.getUsername());
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            account.setEmail(accountDto.getEmail());
            account.setSecondName(accountDto.getMiddleName());
            account.setJobTitle(accountDto.getJobTitle());
            account.setCompanyName(accountDto.getCompanyTitle());
            account.setActive(false);
            account.setBiography(accountDto.getBiography());
            account.setPublishBio(false);
            account.setOneTimePassword(String.valueOf(nextInt));

            MessageRequestDto messageRequestDto= new MessageRequestDto();
            messageRequestDto.setMessage("Your verification code is " + nextInt + " \n Use the code to activate your account");
            messageRequestDto.setSenderId("15200");
//            messageRequestDto.setReceiversPhoneNumber(accountDto.getPhoneNumber());
            messageRequestDto.setReceiversPhoneNumber("+255716521848");
            messageRequestDto.setDateOTPSent(LocalDate.now().toString());
            Response<MessageResponseDto> responseMessage =  bulkSmsIntegration.sendMessage(messageRequestDto);

            account.setLastOtpSent(LocalDateTime.now());
            accountRepository.save(account);
            return new Response<>(false, ResponseCode.SUCCESS,"User registered successfully", account);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern p = Pattern.compile(emailRegex);
        return email != null && p.matcher(email).matches();
    }

    public boolean isValidPhoneNumber(String number) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(number);
        return !matcher.find();
    }
    @Override
    public Response<LoginResponseDto> revokeToken(String refreshToken) {
        try {
            Optional<Account> userAccountOptional = accountRepository.findFirstByRefreshToken(refreshToken);
            if(userAccountOptional.isPresent()){
                return getLoginResponse(userAccountOptional, "" , "");
            }
            return new Response<>(true, ResponseCode.NOT_FOUND, "Invalid refresh token");
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid refresh token");
    }

    @Override
    public Response<Boolean> forgotPassword(String email) {
        return null;
    }

    @Override
    public Response<Account> getLoggedUser() {
        try{
            Account user = loggedUser.getUserAccount();
            if(user == null){
                return new Response<>(true,ResponseCode.UNAUTHORIZED, "Unauthorized" );
            }
        return new Response<>(false,ResponseCode.SUCCESS,"Successfully logged in");

        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new Response<>(true,ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<Account> updatePassword(ChangePasswordDto changePasswordDto) {
        Account user =  loggedUser.getUserAccount();
        if(user == null){
            return new Response<>(true,ResponseCode.UNAUTHORIZED, "Unauthorized");
        } else{
            try{
                if(!passwordEncoder.matches(changePasswordDto.getCurrentPassword(),user.getPassword())){
                    return new Response<>(true, ResponseCode.NOT_ACCEPTABLE,"Password does not match");
                }
                if(changePasswordDto.getNewPassword().isBlank() || changePasswordDto.getNewPassword().length() < 8){
                    return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Password is either too short or null");
                }
                if(!Objects.equals(changePasswordDto.getNewPassword(),changePasswordDto.getConfirmPassword())){
                    return new Response<>(true, ResponseCode.NOT_ACCEPTABLE,"Password does not match");
                }

                user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
                Account savedUser = accountRepository.save(user);
                return new Response<>(true,ResponseCode.SUCCESS,"Password updated successfully");

            }catch (Exception e){
                log.warn(e.getMessage());
            }
        }
        return new Response<>(true,ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<String> activateAccount(String code) {
       try {
           Optional<Account>  userAccountOptional= accountRepository.findByOneTimePassword(code);
           if(userAccountOptional.isPresent()){
               Account account = userAccountOptional.get();
               account.setActive(true);
               accountRepository.save(account);
               return new Response<>(false, ResponseCode.SUCCESS, "User activated successfully");
           }else {
               return new Response<>(true, ResponseCode.UNAUTHORIZED, "No user with this " + code +" exists");
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<String> requestOTP(String phoneNumber) {
        try {
            Optional<Account>  userAccountOptional = accountRepository.findFirstByPhoneNumber(phoneNumber);
            if(userAccountOptional.isPresent()){
                Account account = userAccountOptional.get();
                Random random = new SecureRandom();
                int nextInt = random.nextInt(100001 , 999999);
                account.setOneTimePassword(String.valueOf(nextInt));
                accountRepository.save(account);
                return new Response<>(true, ResponseCode.SUCCESS, "OTP verified successfully");
            }
            return new Response<>(false, ResponseCode.BAD_REQUEST, "No user with this " + phoneNumber +" exists");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<Account> loginByEmail(String email) {
        return null;
    }

    private Response<LoginResponseDto> getLoginResponse(Optional<Account> userAccountOptional, String jwtToken, String refreshToken) {
        if (userAccountOptional.isPresent()) {
            Account account = userAccountOptional.get();
            account.setRefreshToken(refreshToken);
            account.setLastLoginTime(LocalDateTime.now());
            account.setRefreshTokenTime(LocalDateTime.now());
            accountRepository.save(account);

            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    jwtToken,
                    account.getId(),
                    account.getEmail(),
                    account.getUuid(),
                    refreshToken,
                    "Bearer",
                    account.getUserName(),
                    account.getUserType(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhoneNumber(),
                    account.getJobTitle(),
                    account.getCompanyName(),
                    account.getLastLoginTime()
            );

            return new Response<>(false, ResponseCode.SUCCESS, loginResponseDto, null, "Login successful");
        }

        return new Response<>(false, ResponseCode.NOT_FOUND, "Incorrect login credentials");
    }

    public Response<LoginResponseDto> generateNewAccessToken(String refreshToken, LoginDto loginDto){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtils.generateJwtToken(authentication);
            String newRefreshToken = UUID.randomUUID().toString();

            Optional<Account> accountOptional = accountRepository.findFirstByUserName(loginDto.getUsername());

            if(accountOptional.isEmpty())
                return new Response<>(true, ResponseCode.NOT_FOUND,"Invalid user credentials");

            Account account = accountOptional.get();
            if(!account.getActive()) {
                log.info("ACCOUNT NOT ACTIVATED");
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Please activate your account first");
            }
            else {
                return this.getLoginResponse(accountOptional, jwtToken, newRefreshToken);
            }
        }catch (Exception e){
            return new Response<>(true , ResponseCode.BAD_REQUEST , "Unknown error occurred");
        }
    }
}
