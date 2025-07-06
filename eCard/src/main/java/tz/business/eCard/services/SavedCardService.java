package tz.business.eCard.services;

import tz.business.eCard.models.SavedCard;

import java.util.List;


public interface SavedCardService {

    public SavedCard saveCard(Long userId, Long cardId);

    public void unsaveCard(Long userId, Long cardId);

    public List<SavedCard> findByCardId(Long cardId) ;

    public List<SavedCard> findByUserId(Long userId) ;
}