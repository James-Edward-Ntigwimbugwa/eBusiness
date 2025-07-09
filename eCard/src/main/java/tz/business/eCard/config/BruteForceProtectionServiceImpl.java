package tz.business.eCard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.AccountRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {
    private final int maxLogins = 100;
    private final ConcurrentHashMap<String , FailedLogin> cache ;

    public BruteForceProtectionServiceImpl() {
        int maxCacheLimit = 3;
        cache = new ConcurrentHashMap<>(maxCacheLimit);
    }
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void registerLoginFailure(String username) {
        Optional<Account> optionalUserAccount = accountRepository.findFirstByUserName(username);
        if (optionalUserAccount.isPresent()) {
            Account account = optionalUserAccount.get();
            int loginAttempts = account.getLoginAttempts();
            if(loginAttempts >= maxLogins) {
                account.setAccountLocked(true);
                account.setLastLoginTime(LocalDateTime.now());
                accountRepository.save(account);
            } else{
                account.setAccountLocked(false);
                account.setLastLoginTime(LocalDateTime.now());
                account.setLoginAttempts(loginAttempts + 1);
                accountRepository.save(account);
            }
        }
    }

    @Override
    public void resetBruteForceCounter(String username) {
        Optional<Account> userAccountOptional = accountRepository.findFirstByUserName(username);
        if (userAccountOptional.isPresent()) {
            Account account = userAccountOptional.get();
            account.setAccountLocked(false);
            account.setLoginAttempts(0);
            accountRepository.save(account);
        }
    }

    @Override
    public boolean isBruteForceAttack(String username) {
        Optional<Account> userAccountOptional = accountRepository.findFirstByUserName(username);
        if(userAccountOptional.isPresent()) {
            Account account = userAccountOptional.get();
            return account.getLoginAttempts() >= maxLogins;
        }
        return false;
    }

    public FailedLogin failedLogin(String username) {
        FailedLogin failedLogin = cache.get(username);
        if (failedLogin != null) {
            return failedLogin;
        }else {
            failedLogin = new FailedLogin(0 , LocalDateTime.now());
            cache.put(username.toLowerCase(), failedLogin);
        }
        return failedLogin;
    }
}
