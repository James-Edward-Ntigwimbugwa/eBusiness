package tz.business.eCard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;
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
    private UserAccountRepository userAccountRepository;

    @Override
    public void registerLoginFailure(String username) {
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findFirstByUserName(username);
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            int loginAttempts = userAccount.getLoginAttempts();
            if(loginAttempts >= maxLogins) {
                userAccount.setAccountLocked(true);
                userAccount.setLastLoginTime(LocalDateTime.now());
                userAccountRepository.save(userAccount);
            } else{
                userAccount.setAccountLocked(false);
                userAccount.setLastLoginTime(LocalDateTime.now());
                userAccount.setLoginAttempts(loginAttempts + 1);
                userAccountRepository.save(userAccount);
            }
        }
    }

    @Override
    public void resetBruteForceCounter(String username) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findFirstByUserName(username);
        if (userAccountOptional.isPresent()) {
            UserAccount userAccount = userAccountOptional.get();
            userAccount.setAccountLocked(false);
            userAccount.setLoginAttempts(0);
            userAccountRepository.save(userAccount);
        }
    }

    @Override
    public boolean isBruteForceAttack(String username) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findFirstByUserName(username);
        if(userAccountOptional.isPresent()) {
            UserAccount userAccount = userAccountOptional.get();
            return userAccount.getLoginAttempts() >= maxLogins;
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
