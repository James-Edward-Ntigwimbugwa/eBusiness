package tz.business.eCard;

import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tz.business.eCard.config.TwilioConfig;


/**
 * @author JAMES EDWARD
 *
 * @January 19 , 2025
 */

@SpringBootApplication(scanBasePackages = "tz.business.eCard")
//@SpringBootApplication
public class ECardApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(ECardApplication.class, args);
	}

	@Autowired
	public void verifyTwilioIsInitialized(TwilioConfig twilioConfig) {
		if (Twilio.getRestClient() == null) {
			throw new RuntimeException("Twilio REST client not initialized");
		}
	}
}
