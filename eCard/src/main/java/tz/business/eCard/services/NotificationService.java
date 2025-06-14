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

public interface NotificationService {

    public void createCardSavedNotification(UserAccount actor, Cards card);

    public List<Notification> getUserNotifications(Long userId) ;

    public long countUnreadNotifications(Long userId);

    public void markAsRead(Long notificationId) ;

    public void markAllAsRead(Long userId) ;

}
