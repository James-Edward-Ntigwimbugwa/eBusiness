package tz.business.eCard.ServiceImpls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.Card;
import tz.business.eCard.models.SavedCard;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.repositories.SavedCardRepository;
import tz.business.eCard.repositories.AccountRepository;
import tz.business.eCard.services.NotificationService;
import tz.business.eCard.services.SavedCardService;
import java.util.List;


@Service
@Slf4j
public class SavedCardServiceImpl implements SavedCardService {
    @Autowired
    private SavedCardRepository savedCardRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AccountRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    public SavedCard saveCard(Long userId, Long cardId) {
        // Check if the card is already saved
        if (savedCardRepository.existsByUserIdAndCardId(userId, cardId)) {
            throw new RuntimeException("Card already saved by this user");
        }

        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        SavedCard savedCard = new SavedCard((Account) user, card);
        SavedCard result = savedCardRepository.save(savedCard);

        notificationService.createCardSavedNotification((Account) user, card);

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
