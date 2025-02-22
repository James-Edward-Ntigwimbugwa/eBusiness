package tz.business.eCard.ServiceImpls;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.business.eCard.dtos.CardDto;
import tz.business.eCard.dtos.GroupCardsDto;
import tz.business.eCard.dtos.MyCardDto;
import tz.business.eCard.models.Cards;
import tz.business.eCard.services.CardService;
import tz.business.eCard.utils.Response;

public class CardServiceImpl implements CardService {
    @Override
    public Response<Cards> createCard(CardDto cardDto) {
        return null;
    }

    @Override
    public Response<Cards> updateCard(CardDto cardDto) {
        return null;
    }

    @Override
    public Response<Cards> deleteCard(String cardId) {
        return null;
    }

    @Override
    public Page<Cards> getAllCards(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Cards> searchCardsByTitle(String title, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Cards> getAllActiveCards(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Cards> getAllPublicActiveCards(Pageable pageable) {
        return null;
    }

    @Override
    public Response<Cards> getCardByUuid(String uuid) {
        return null;
    }

    @Override
    public Page<Cards> getCardsByUserUuid(Pageable pageable) {
        return null;
    }

    @Override
    public Response<Cards> saveCard(MyCardDto myCardDto) {
        return null;
    }

    @Override
    public Response<Cards> groupCards(GroupCardsDto groupCardsDto) {
        return null;
    }
}
