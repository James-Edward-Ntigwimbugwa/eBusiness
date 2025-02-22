package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardLinksDto {
    private String cardsUuid;
    private String userUuid;
    private String url;
    private String  qrCodeUrl;
}
