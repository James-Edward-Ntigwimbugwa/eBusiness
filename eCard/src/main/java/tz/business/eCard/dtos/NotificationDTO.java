package tz.business.eCard.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.business.eCard.models.Cards;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String type;
    private Long actorId;
    private String actorName;
    private Long cardId;
    private String cardName;
    private LocalDateTime createdAt;
}