package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CustomCardDto {
    private String firstName;
    private  String lastName;
    private String cardImage ;
}
