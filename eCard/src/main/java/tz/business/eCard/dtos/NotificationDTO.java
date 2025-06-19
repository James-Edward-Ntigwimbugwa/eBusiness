package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long recipientId;
    private Long actorId;
    private Long cardId;
    private String type;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

}