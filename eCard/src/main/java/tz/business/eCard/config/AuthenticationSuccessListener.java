package tz.business.eCard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private Logger logger = LoggerFactory.getLogger(AuthenticationSuccessListener.class);
    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logger.info("LOGIN SUCCESS fOR USER======> {}", username);
        bruteForceProtectionService.resetBruteForceCounter(username);
    }
}
