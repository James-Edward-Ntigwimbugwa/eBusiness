package tz.business.eCard.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.business.eCard.dtos.CardDto;
import tz.business.eCard.dtos.GroupCardsDto;
import tz.business.eCard.dtos.MyCardDto;
import tz.business.eCard.models.Card;
import tz.business.eCard.utils.Response;

public interface CardService {
    Card findById(Long id);

    Response<Card> createCard(CardDto cardDto);
    Response<Card> updateCard(CardDto cardDto);
    Response<Boolean> deleteCard(String cardId);
    Page<CardDto> getAllCards(Pageable pageable);
    Page<Card>  searchCardsByTitle(String title, Pageable pageable);
    Page<Card> getAllActiveCards(Pageable pageable);
    Page<Card> getAllPublicActiveCards(Pageable pageable);
    Response<Card> getCardByUuid(String uuid);
    Page<Card> getCardsByUserUuid(String uuid , Pageable pageable);
    Response<Card> saveCard(MyCardDto myCardDto);
    Response<Card> groupCards(GroupCardsDto groupCardsDto);

}
