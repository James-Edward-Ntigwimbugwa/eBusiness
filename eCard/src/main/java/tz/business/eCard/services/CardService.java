package tz.business.eCard.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.business.eCard.dtos.CardDto;
import tz.business.eCard.dtos.GroupCardsDto;
import tz.business.eCard.dtos.MyCardDto;
import tz.business.eCard.models.Cards;
import tz.business.eCard.utils.Response;

public interface CardService {
    Cards findById(Long id);

    Response<Cards> createCard(CardDto cardDto);
    Response<Cards> updateCard(CardDto cardDto);
    Response<Boolean> deleteCard(String cardId);
    Page<CardDto> getAllCards(Pageable pageable);
    Page<Cards>  searchCardsByTitle(String title, Pageable pageable);
    Page<Cards> getAllActiveCards(Pageable pageable);
    Page<Cards> getAllPublicActiveCards(Pageable pageable);
    Response<Cards> getCardByUuid(String uuid);
    Page<Cards> getCardsByUserUuid(String uuid , Pageable pageable);
    Response<Cards> saveCard(MyCardDto myCardDto);
    Response<Cards> groupCards(GroupCardsDto groupCardsDto);

}
