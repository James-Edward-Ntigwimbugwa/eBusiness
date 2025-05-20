package tz.business.eCard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.NotificationDTO;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.Notification;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createCardSavedNotification(UserAccount actor, Cards card) {
        UserAccount cardHolder = card.getUser(); // Assuming Card has a reference to its holder

        Notification notification = new Notification(cardHolder, actor, card, "CARD_SAVED");
        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification
        sendRealTimeNotification(savedNotification);

        return savedNotification;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
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
