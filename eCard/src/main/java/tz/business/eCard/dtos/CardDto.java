package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private String uuid;
    private String title;
    private String organization;
    private String address;
    private String cardLogo;
    private String phoneNumber;
    private String email;
    private String profilePhoto;
    private String linkedin;  // Note this is "linkedin" not "linkedIn" to match Flutter field
    private String websiteUrl;
    private String department;
    private String cardDescription;
    private String backgroundColor;
    private String fontColor;
    private boolean publishCard;
}