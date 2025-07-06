package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.NotificationDTO;
import tz.business.eCard.models.Card;
import tz.business.eCard.models.Notification;
import tz.business.eCard.models.SavedCard;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardRepository; // Assuming this exists
import tz.business.eCard.repositories.NotificationRepository;
import tz.business.eCard.repositories.SavedCardRepository; // Assuming this exists
import tz.business.eCard.services.NotificationService;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SavedCardRepository savedCardRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LoggedUser loggedUser;

    public void createCardSavedNotification(UserAccount actor, Card card) {
        UserAccount cardHolder = card.getUser();
        Notification notification = new Notification(cardHolder, actor, card, "CARD_SAVED");
        Notification savedNotification = notificationRepository.save(notification);
        sendRealTimeNotification(savedNotification);
    }

    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public void sendNotificationToSavedUsers(Long cardId, String message) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            log.info("user {}", user);
            Card cards = new Card();

            if (user == null) {
                log.info("Unauthorized ======>");
            }

            List<SavedCard> savedCards = savedCardRepository.findByCardId(cardId);
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            UserAccount cardHolder = card.getUser();

            for (SavedCard savedCard : savedCards) {
                UserAccount recipient = savedCard.getUser();
                Notification notification = new Notification();
                notification.setRecipient(recipient);
                notification.setActor(cardHolder);
                notification.setCard(card);
                notification.setType("CARDHOLDER_MESSAGE");
                notification.setMessage(message);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRead(false);

                notificationRepository.save(notification);
                sendRealTimeNotification(notification);
            }
        }catch (Exception e){

        }
    }

    private void sendRealTimeNotification(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setRecipientId(notification.getRecipient().getId());
        dto.setActorId(notification.getActor().getId());
        dto.setCardId(notification.getCard().getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setRead(notification.isRead());

        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getId().toString(),
                "/queue/notifications",
                dto
        );
    }
}