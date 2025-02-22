package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.business.eCard.models.Cards;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class GroupDto {

    private String groupName;
    private  String groupUuid ;
    private List<Cards> cardsList ;
}
