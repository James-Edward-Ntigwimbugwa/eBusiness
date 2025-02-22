package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDto {

    private  String uuid;
    private String firstName;
    private String username;
    private String middleName;
    private String lastName;
    private String email;
    private String userRole;
    private String password;
    private String phoneNumber;
    private String biography;
    private String companyTitle ;
    private String jobTitle;

    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

}
