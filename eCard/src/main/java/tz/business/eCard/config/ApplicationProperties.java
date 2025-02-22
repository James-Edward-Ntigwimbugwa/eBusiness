package tz.business.eCard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "tz.business.ecard")
public class ApplicationProperties {
    private String jwtKey;

    private String defaultPassword;

    private int maxLoginAttempts = 3;

    private  int cacheMaxLimit = 1000;

    private String apiKey ;

    private String secretKey;

    private String sourceAddress;

    private String downStreamUrl;
}
