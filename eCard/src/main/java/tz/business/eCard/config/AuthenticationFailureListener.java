package tz.business.eCard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);
    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        log.info("LOGIN FAILED FOR USER=============>{}",username);

        bruteForceProtectionService.registerLoginFailure(username);
    }
}
