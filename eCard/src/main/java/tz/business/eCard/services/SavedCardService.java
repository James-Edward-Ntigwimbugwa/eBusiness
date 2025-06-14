package tz.business.eCard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.SavedCard;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.repositories.SavedCardRepository;
import tz.business.eCard.repositories.UserAccountRepository;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;

import java.util.List;


public interface SavedCardService {

    public SavedCard saveCard(Long userId, Long cardId);

    public void unsaveCard(Long userId, Long cardId);

    public List<SavedCard> findByCardId(Long cardId) ;

    public List<SavedCard> findByUserId(Long userId) ;
}