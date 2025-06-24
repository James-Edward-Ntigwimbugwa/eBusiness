package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDto {
    private Long id;
    private String uuid ;
    private  String title ;
    private  String cardDescription ;
    private  boolean publishCard ;
    private  String organization ;
    private String cardLogo;
    private String textPosition;
    private  String logoPosition ;
    private  String fontStyle ;
    private String latitude ;
    private  String longitude ;
    private String profilePhoto ;
    private String phoneNumber ;
    private  String email ;
    private  String linkedin;
    private String websiteUrl;
    private String department ;
    private  String backgroundColor;
    private String fontColor;
    
    

}
