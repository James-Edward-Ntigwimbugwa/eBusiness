package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NearbyCardInfo {
    private String cardId;
    private String title;
    private String organization;
    private String email;
    private String phoneNumber;
    private String cardLogo;
    private String profilePhoto;
    private double distance;
    private double latitude;
    private double longitude;
}