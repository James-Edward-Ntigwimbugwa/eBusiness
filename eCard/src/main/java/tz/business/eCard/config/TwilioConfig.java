package tz.business.eCard.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${account.twilio.sid}")
    private String accountSid;

    @Value("${account.twilio.authToken}")
    private String accountToken;

    @PostConstruct
    public void initializeTwilio() {
        try {
            Twilio.init(accountSid, accountToken);
        }catch (Exception e) {
            System.out.println("Error initializing twilio =====>"+e.getMessage());
            e.printStackTrace();
        }

    }
}
