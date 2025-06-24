
package tz.business.eCard;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class Init implements ApplicationRunner{
    private final UserAccountRepository userAccountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            UserAccount userAccount;
            Optional<UserAccount> admin = userAccountRepository.findFirstByUserName("admin@eCard.go.tz");
            if(admin.isEmpty()){
                log.info("\n\n =========================================================================================\n\n" +
                        "\t\t INITIALIZING AND CREATING DEFAULT ADMIN USER \n\n " +
                        " =========================================================================================");

                userAccount = new UserAccount();
                userAccount.setUserName("admin@eCard.go.tz");
                userAccount.setLastName("eCard");
                userAccount.setFirstName("admin");
                userAccount.setFullName("null");
                userAccount.setSecondName("null");
                userAccount.setActive(true);
                userAccount.setUserType("SUPER_ADMIN");
                userAccount.setEnabled(true);
                userAccount.setCompanyName("eCard");
                userAccount.setJobTitle("Developer");
                userAccount.setPassword("admin@ntigwimnugwa2001!");
                userAccount.setPhoneNumber("255712121212");
                userAccount.setEmail("admin.go.tz@ecard.co.tz");
                userAccountRepository.save(userAccount);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }        
    }

    
}