package tz.business.eCard.utils.userExtractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.UserAccountRepository;

import java.util.HashMap;

@Slf4j
@Service
public class LoggedUserImp implements LoggedUser {

    private static  final Logger logger = LoggerFactory.getLogger(LoggedUserImp.class);
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private ObjectMapper objectMapper;
    public LoggedUserImp(){
        this.objectMapper = new ObjectMapper().registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }
    @Override
    public UserInfo getInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.info("Null Auth detected");
            return null;
        } else {
            try {
                String jsonAuth = objectMapper.writeValueAsString(auth);
                HashMap<String, Object> result = objectMapper.readValue(jsonAuth, HashMap.class);

                if (result.get("principal") != null) {
                    Object principal = result.get("principal");
                    if (principal.toString().contains("anonymousUser")) {
                        // Handle anonymous user case
                        return null; // You can set appropriate values for an anonymous user
                    }

                    HashMap<String, Object> principalMap = (HashMap<String, Object>) principal;
                    logger.info("User principal found: {}", principalMap);

                    Object id = principalMap.get("id");
                    Object username = principalMap.get("username");
                    Object uuid = principalMap.get("uuid"); // Add this line to get the email

                    return new UserInfo(
                            String.valueOf(Long.parseLong((String) id)), // Pass id as Long
                            uuid != null ? uuid.toString() : null, // Pass email or null,
                            true, // Add your logic for accountNonExpired
                            true, // Add your logic for accountNonLocked
                            true, // Add your logic for credentialsNonExpired
                            true, // Add your logic for enabled
                            username != null ? username.toString() : null // Pass username or null
                    );
                }
            } catch (Exception e) {
                logger.error("Error occurred on authentication facade: {}", e.getMessage());
                log.error("Exception", e);
            }
        }
        return null;
    }

    @Override
    public UserAccount getUserAccount() {
        UserInfo userInfo = getInfo();
        if(userInfo != null){
            return userAccountRepository.findById(Long.valueOf(userInfo.getId())).orElse(null);
        }
        return null;
    }
}
