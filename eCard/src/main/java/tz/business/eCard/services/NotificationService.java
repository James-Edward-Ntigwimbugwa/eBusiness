package tz.business.eCard.services;

import tz.business.eCard.models.Card;
import tz.business.eCard.models.Notification;
import tz.business.eCard.models.UserAccount;

import java.util.List;

public interface NotificationService {
    void createCardSavedNotification(UserAccount actor, Card card);

    List<Notification> getUserNotifications(Long userId);

    long countUnreadNotifications(Long userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    void sendNotificationToSavedUsers(Long cardId, String message);
}