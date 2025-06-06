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

@Service
public class SavedCardService {
    @Autowired
    private SavedCardRepository savedCardRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    public SavedCard saveCard(Long userId, Long cardId) {
        // Check if the card is already saved
        if (savedCardRepository.existsByUserIdAndCardId(userId, cardId)) {
//            return new Response(true , ResponseCode.ALREADY_EXISTS, "Card already saved by this user");
            throw new RuntimeException("Card already saved by this user");
        }

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cards card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        SavedCard savedCard = new SavedCard((UserAccount) user, card);
        SavedCard result = savedCardRepository.save(savedCard);

        notificationService.createCardSavedNotification((UserAccount) user, card);

        return result;
    }

    public void unsaveCard(Long userId, Long cardId) {
        savedCardRepository.deleteByUserIdAndCardId(userId, cardId);
    }

    public List<SavedCard> findByCardId(Long cardId) {
        return savedCardRepository.findByCardId(cardId);
    }

    public List<SavedCard> findByUserId(Long userId) {
        return savedCardRepository.findByUserId(userId);
    }
}