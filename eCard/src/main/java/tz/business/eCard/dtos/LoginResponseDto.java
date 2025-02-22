package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private String tokenType;
    private String username;
    private String userType;
    private String firstName;
    private String lastName;
    private String phone;
    private String jobTitle;
    private String companyName;
    private LocalDateTime lastLogin;

}
