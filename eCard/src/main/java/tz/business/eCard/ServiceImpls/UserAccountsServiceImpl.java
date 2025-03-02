package tz.business.eCard.ServiceImpls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.BioDto;
import tz.business.eCard.dtos.UserAccountDto;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;
import tz.business.eCard.services.UserAccountService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.UserType;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class UserAccountsServiceImpl implements UserAccountService {
    private Logger log = Logger.getLogger(UserAccountsServiceImpl.class.getName());

    @Autowired
    private UserAccountRepository userAccountRepository;
    private UserAccount userAccount;
    @Autowired
    private LoggedUser loggedUser;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Response<UserAccount> createUpdateUserAccount(UserAccountDto userAccountDto) {
        try {
            UserAccount user = loggedUser.getUserAccount();

            if (user == null) {
                log.warning("UNAUTHORIZED USER CREATING USER");
                return  new Response<>(true , ResponseCode.UNAUTHORIZED,"Full Authentication required");
            }

            Optional<UserAccount> accountOptional = userAccountRepository.findFirstByUserName(userAccountDto.getUsername());
            Optional<UserAccount> accountOptional1 = userAccountRepository.findFirstByPhoneNumber(userAccountDto.getPhoneNumber());

            if (accountOptional.isPresent()) {
                return  new Response<>(true , ResponseCode.DUPLICATE_KEY , "UserName Already Exists");
            }

            if (accountOptional1.isPresent()) {
                return  new Response<>(true , ResponseCode.DUPLICATE_KEY , "PhoneNumber Already Exists");
            }

            if (userAccountDto.getPassword() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "Password must not be null");
            }

            if(userAccountDto.getFirstName() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "FirstName must not be null");
            }

            if(userAccountDto.getLastName() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "LastName must not be null");
            }

            if(userAccountDto.getPhoneNumber() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "PhoneNumber must not be null");
            }

            if (userAccountDto.getEmail() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "Email must not be null");
            }

            if (userAccountDto.getCompanyTitle() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "CompanyTitle must not be null");
            }

            if(userAccountDto.getJobTitle() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "JobTitle must not be null");
            }

            if(userAccountDto.getMiddleName()==null) {
                userAccountDto.setMiddleName("");
            }

            else{
                if(!userAccountDto.getMiddleName().isBlank() && !Objects.equals(userAccountDto.getMiddleName(), userAccountDto.getMiddleName())) {
                    userAccountDto.setMiddleName(userAccountDto.getMiddleName());
                }
                if(!userAccountDto.getLastName().isBlank() && !Objects.equals(userAccountDto.getLastName(), userAccountDto.getLastName())) {
                    userAccountDto.setLastName(userAccountDto.getLastName());
                }

                if(!userAccountDto.getFirstName().isBlank() && !Objects.equals(userAccountDto.getFirstName(), userAccountDto.getFirstName())) {
                    userAccountDto.setFirstName(userAccountDto.getFirstName());
                }

                if(!userAccountDto.getPassword().isBlank() && !Objects.equals(userAccountDto.getPassword(), userAccountDto.getPassword())) {
                    userAccountDto.setPassword(userAccountDto.getPassword());
                }
                if(!userAccountDto.getJobTitle().isBlank() && !Objects.equals(userAccountDto.getJobTitle(), userAccountDto.getJobTitle())) {
                    userAccountDto.setJobTitle(userAccountDto.getJobTitle());
                }
                if(!userAccountDto.getCompanyTitle().isBlank() && !Objects.equals(userAccountDto.getCompanyTitle(), userAccountDto.getCompanyTitle())) {
                    userAccountDto.setCompanyTitle(userAccountDto.getCompanyTitle());
                }
                if(!userAccountDto.getPhoneNumber().isBlank() && !Objects.equals(userAccountDto.getPhoneNumber(), userAccountDto.getPhoneNumber())) {
                    userAccountDto.setPhoneNumber(userAccountDto.getPhoneNumber());
                }
                if(!userAccountDto.getPassword().isBlank() && !Objects.equals(userAccountDto.getPassword(), userAccountDto.getPassword())) {
                    userAccountDto.setPassword(passwordEncoder.encode(userAccountDto.getPassword()));
                }
                if(userAccountDto.getUserRole() == null){
                    userAccount.setUserType(String.valueOf(UserType.CUSTOMER));
                }
                else if(userAccountDto.getUserRole().equalsIgnoreCase(UserType.ADMIN.name()))
                    userAccount.setUserType(String.valueOf(UserType.ADMIN));
                else if (userAccountDto.getUserRole().equalsIgnoreCase(UserType.SUPER_ADMIN.name()))
                    userAccount.setUserType(String.valueOf(UserType.SUPER_ADMIN));
                else if (userAccountDto.getUserRole().equalsIgnoreCase(UserType.SELLER.name()))
                    userAccount.setUserType(String.valueOf(UserType.SELLER));
                else if(userAccountDto.getUserRole().equalsIgnoreCase(UserType.VENDOR.name()))
                    userAccount.setUserType(String.valueOf(UserType.VENDOR));
                else userAccount.setUserType(String.valueOf(UserType.CUSTOMER));

                UserAccount savedUser = userAccountRepository.save(userAccount);
                return new Response<>(false,ResponseCode.SUCCESS,savedUser);
            }
        } catch (Exception e){
            log.warning(e.getMessage());
        }

        return new Response<>(false , ResponseCode.UNAUTHORIZED , "Unauthorized");
    }

    @Override
    public Response<UserAccount> deleteUserAccount(String uuid) {

        try {
            UserAccount user = loggedUser.getUserAccount();

            if(user == null) {
                log.warning("UNAUTHORIZED USER DELETING USER");
                return  new Response<>(true , ResponseCode.UNAUTHORIZED , "Full Authentication required");
            }
            Optional<UserAccount> userAccount =userAccountRepository.findFirstByUuid(uuid);
            if(userAccount.isPresent()) {
                userAccountRepository.delete(userAccount.get());
                return new Response<>(false, ResponseCode.SUCCESS, "User Account Deleted");
            } else {
                return new Response<>(true, ResponseCode.NOT_FOUND, "No user found with id " + uuid);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new Response<>(true, ResponseCode.UNAUTHORIZED , "Unauthorized");
    }

    @Override
    public Response<UserAccount> getUserByUuid(String uuid) {
        try {
            Optional<UserAccount> userAccount =userAccountRepository.findFirstByUuid(uuid);
            return userAccount.map(account -> new Response<>(true, ResponseCode.SUCCESS, "User with id " + uuid + " found", account)).orElseGet(() -> new Response<>(true, ResponseCode.NOT_FOUND, "No user found with id " + uuid));

        } catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST , "Bad Request");
    }

    @Override
    public Page<UserAccount> getAllUserAccounts(Pageable pageable) {
        try {
          return userAccountRepository.findALlByDeletedFalse(pageable);

        }catch (Exception e){
            log.warning(e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<UserAccount> getOfficials(Pageable pageable) {
        try{
            return userAccountRepository.findAllByUserTypeNot(String.valueOf(UserType.CUSTOMER), pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<UserAccount> getCustomers(Pageable pageable) {
        try{
            return userAccountRepository.findAllByUserTypeAndDeletedFalse(String.valueOf(UserType.CUSTOMER), pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<UserAccount> getVendors(Pageable pageable) {
        try {
            return userAccountRepository.findAllByUserTypeAndDeletedFalse(String.valueOf(UserType.VENDOR), pageable);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Response<UserAccount> updateBio(BioDto bioDto) {
        try{
            if(bioDto.getBio() == null){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "Bio is null");
            }
            if(bioDto.getUserUuid() == null){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "User UUID is null");
            }

            Optional<UserAccount>accountOptional = userAccountRepository.findFirstByUuid(bioDto.getUserUuid());

            if(accountOptional.isPresent()) {
                return new Response<>(true, ResponseCode.ALREADY_EXISTS, "User with "+bioDto.getUserUuid() + " already exists");
            }
            UserAccount account = accountOptional.get();
            account.setBiography(bioDto.getBio());
            account.setPublishBio(bioDto.isPublishBio());
            UserAccount savedUser = userAccountRepository.save(account);
            return new Response<>(true, ResponseCode.SUCCESS, "User with "+bioDto.getUserUuid() + " updated", savedUser);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST , "Bad Request");
    }
}
