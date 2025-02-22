package tz.business.eCard.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "LoginDto [username=" + username + ", password=" + password + "]";
    }

}
