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
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;
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
    private UserAccountRepository userAccountRepository;
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

            Optional<UserAccount> accountOptional = userAccountRepository.findFirstByUserName(loginDto.getUsername());

            if(accountOptional.isEmpty())
                return new Response<>(true, ResponseCode.NOT_FOUND,"Invalid login credentials");

            UserAccount userAccount = accountOptional.get();
            if(!userAccount.getActive()) {
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
    public Response<UserAccount> register(UserAccountDto userAccountDto) {
        try {
            if(userAccountDto.getFirstName() == null || userAccountDto.getLastName()==null){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Invalid first name or last name");
            }

            if(userAccountDto.getFirstName().isEmpty() || userAccountDto.getLastName().isEmpty()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "First name and last name are required");
            }

            if(userAccountDto.getPassword().isBlank()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Password is required");
            }

            if(userAccountDto.getPhoneNumber()== null || userAccountDto.getPhoneNumber().isEmpty()){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT, "Phone number is required");
            }

            if(!isValidEmail(userAccountDto.getEmail())){
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid email");
            }
            if(!isValidPhoneNumber(userAccountDto.getPhoneNumber())){
                return new Response<>(true , ResponseCode.BAD_REQUEST,"Invalid phone number");
            }

            if(userAccountDto.getJobTitle()== null || userAccountDto.getJobTitle().isEmpty()){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Job title is required");
            }

            if(userAccountDto.getCompanyTitle()==null||userAccountDto.getCompanyTitle().isEmpty()){
                return  new Response<>(true, ResponseCode.NULL_ARGUMENT, "Company title is required");
            }

            Optional<UserAccount> userAccountOptional = userAccountRepository.findFirstByPhoneNumber(userAccountDto.getPhoneNumber());
            if(userAccountOptional.isPresent()){
                return  new Response<>(true,ResponseCode.DUPLICATE_KEY , "Account with this phone number already exist");
            }
            Optional<UserAccount> userAccountOptional1 = userAccountRepository.findFirstByEmail(userAccountDto.getEmail());
            if(userAccountOptional1.isPresent()){
                return new Response<>(true, ResponseCode.DUPLICATE_KEY, "Account with this email already exist");
            }
//        Optional<UserAccount> userAccountOptional2 = userAccountRepository.findFirstByUsername(userAccountDto.getFirstName() + " " + userAccountDto.getMiddleName() + " " + userAccountDto.getLastName());
//        if(userAccountOptional2.isPresent()){
//            return new Response<>(true, ResponseCode.DUPLICATE_KEY, "Account with username already exist");
//        }

            UserAccount account = new UserAccount();
            Random rand = new SecureRandom();
            int nextInt = rand.nextInt(100001 , 999999);
            if(userAccountDto.getUserRole()==null || userAccountDto.getUserRole().isEmpty()){
                account.setUserType(UserType.CUSTOMER.toString());
            }
            else{
                account.setUserType(userAccountDto.getUserRole());
            }

            account.setEnabled(false);
            account.getUuid();
            account.setFirstName(userAccountDto.getFirstName());
            account.setLastName(userAccountDto.getLastName());
            account.setFullName(userAccountDto.getFirstName() + " " +userAccountDto.getMiddleName() +" " + userAccountDto.getLastName());
            account.setPhoneNumber(userAccountDto.getPhoneNumber());
            account.setUserName(userAccountDto.getUsername());
            account.setPassword(passwordEncoder.encode(userAccountDto.getPassword()));
            account.setEmail(userAccountDto.getEmail());
            account.setSecondName(userAccountDto.getMiddleName());
            account.setJobTitle(userAccountDto.getJobTitle());
            account.setCompanyName(userAccountDto.getCompanyTitle());
            account.setActive(false);
            account.setBiography(userAccountDto.getBiography());
            account.setPublishBio(false);
            account.setOneTimePassword(String.valueOf(nextInt));

            MessageRequestDto messageRequestDto= new MessageRequestDto();
            messageRequestDto.setMessage("Your verification code is " + nextInt + " \n Use the code to activate your account");
            messageRequestDto.setSenderId("15200");
            messageRequestDto.setReceiversPhoneNumber(userAccountDto.getPhoneNumber());
            messageRequestDto.setDateOTPSent(LocalDate.now().toString());
            Response<MessageResponseDto> responseMessage =  bulkSmsIntegration.sendMessage(messageRequestDto);

            account.setLastOtpSent(LocalDateTime.now());
            userAccountRepository.save(account);
            return new Response<>(false, ResponseCode.SUCCESS,"User registered successfully", account);
        }catch (Exception e){
            e.printStackTrace();
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
        return matcher.find();
    }
    @Override
    public Response<LoginResponseDto> revokeToken(String refreshToken) {
        try {
            Optional<UserAccount> userAccountOptional = userAccountRepository.findFirstByRefreshToken(refreshToken);
            if(userAccountOptional.isPresent()){
                return getLoginResponse(userAccountOptional, "" , "");
            }
            return new Response<>(true, ResponseCode.NOT_FOUND, "Invalid refresh token");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid refresh token");
    }

    @Override
    public Response<Boolean> forgotPassword(String email) {
        return null;
    }

    @Override
    public Response<UserAccount> getLoggedUser() {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null){
                return new Response<>(true,ResponseCode.UNAUTHORIZED, "Unauthorized" );
            }
        return new Response<>(true,ResponseCode.SUCCESS,"Successfully logged in");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<UserAccount> updatePassword(ChangePasswordDto changePasswordDto) {
        UserAccount user =  loggedUser.getUserAccount();
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
                UserAccount savedUser = userAccountRepository.save(user);
                return new Response<>(true,ResponseCode.SUCCESS,"Password updated successfully");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return new Response<>(true,ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<String> activateAccount(String code) {
       try {
           Optional<UserAccount>  userAccountOptional= userAccountRepository.findByOneTimePassword(code);
           if(userAccountOptional.isPresent()){
               UserAccount userAccount = userAccountOptional.get();
               userAccount.setActive(true);
               userAccountRepository.save(userAccount);
               return new Response<>(true, ResponseCode.SUCCESS, "User activated successfully");
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
            Optional<UserAccount>  userAccountOptional = userAccountRepository.findFirstByPhoneNumber(phoneNumber);
            if(userAccountOptional.isPresent()){
                UserAccount userAccount = userAccountOptional.get();
                Random random = new SecureRandom();
                int nextInt = random.nextInt(100001 , 999999);
                userAccount.setOneTimePassword(String.valueOf(nextInt));
                userAccountRepository.save(userAccount);
                return new Response<>(true, ResponseCode.SUCCESS, "OTP verified successfully");
            }
            return new Response<>(false, ResponseCode.BAD_REQUEST, "No user with this " + phoneNumber +" exists");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<UserAccount> loginByEmail(String email) {
        return null;
    }

    private Response<LoginResponseDto> getLoginResponse(Optional<UserAccount> userAccountOptional, String jwtToken, String refreshToken) {
        if (userAccountOptional.isPresent()) {
            UserAccount userAccount = userAccountOptional.get();
            userAccount.setRefreshToken(refreshToken);
            userAccount.setLastLoginTime(LocalDateTime.now());
            userAccount.setRefreshTokenTime(LocalDateTime.now());
            userAccountRepository.save(userAccount);

            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    jwtToken,
                    refreshToken,
                    "Bearer",
                    userAccount.getUserName(),
                    userAccount.getUserType(),
                    userAccount.getFirstName(),
                    userAccount.getLastName(),
                    userAccount.getPhoneNumber(),
                    userAccount.getCompanyName(),
                    userAccount.getJobTitle(),
                    userAccount.getLastLoginTime()
            );

            return new Response<>(false, ResponseCode.SUCCESS, loginResponseDto, null, "Login successful");
        }

        return new Response<>(false, ResponseCode.NOT_FOUND, "Incorrect login credentials");
    }
}
