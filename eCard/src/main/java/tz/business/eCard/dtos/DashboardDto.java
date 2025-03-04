package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    private Long myCards;
    private Long myContacts;
    private Long myFavoritesCards;
    private Long registeredCards;
    private Long getMyContacts;
    private Long activeCards;
}
