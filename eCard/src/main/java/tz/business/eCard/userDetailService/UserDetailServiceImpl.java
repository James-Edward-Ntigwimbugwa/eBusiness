package tz.business.eCard.userDetailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;

@Component
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserDetailServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findFirstByUserName(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));
        return UserDetailsImpl.build(userAccount);
    }
}
