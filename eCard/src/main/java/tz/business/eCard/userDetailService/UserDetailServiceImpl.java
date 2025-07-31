package tz.business.eCard.userDetailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.AccountRepository;

@Component
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findFirstByUserName(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));
        return UserDetailsImpl.build(account);
    }
}