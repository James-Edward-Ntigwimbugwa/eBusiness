package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.NotificationDTO;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.Notification;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.NotificationRepository;
import tz.business.eCard.services.NotificationService;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public  void createCardSavedNotification(UserAccount actor , Cards card){
        {
            UserAccount cardHolder = card.getUser();

            Notification notification = new Notification(cardHolder, actor, card, "CARD_SAVED");
            Notification savedNotification = notificationRepository.save(notification);

            // Send real-time notification
            sendRealTimeNotification(savedNotification);

        }

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

    public List<Notification> getUserNotifications(Long userId){
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    private void sendRealTimeNotification(Notification notification) {
        // Convert notification to DTO for sending
        NotificationDTO dto = new NotificationDTO();

        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getId().toString(),
                "/queue/notifications",
                dto
        );
    }

}
