package tz.business.eCard;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.AccountRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitRunner implements ApplicationRunner{
    private final AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            Account account;
            Optional<Account> admin = accountRepository.findFirstByUserName("admin@eCard.api.tz");
            if(admin.isEmpty()){
                log.info("\n\n =========================================================================================\n\n" +
                        "\t\t INITIALIZING AND CREATING DEFAULT ADMIN USER \n\n " +
                        " =========================================================================================");

                account = new Account();
                account.setUserName("admin@eCard.api.tz");
                account.setLastName("eCard");
                account.setFirstName("admin");
                account.setFullName("null");
                account.setSecondName("null");
                account.setActive(true);
                account.setUserType("SUPER_ADMIN");
                account.setEnabled(true);
                account.setCompanyName("eCard");
                account.setJobTitle("Developer");
                account.setPassword("admin@ntigwimnugwa2001!");
                account.setPhoneNumber("255712121212");
                account.setEmail("admin.api.tz@ecard.co.tz");
                accountRepository.save(account);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }        
    }

}