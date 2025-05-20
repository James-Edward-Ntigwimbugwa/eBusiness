package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserAccount recipient; // The card holder who receives the notification

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private UserAccount actor; // The user who saved the card

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Cards card; // The card that was saved

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "type")
    private String type; // For example, "CARD_SAVED"

    public Notification(UserAccount recipient, UserAccount actor, Cards card, String type) {
        this.recipient = recipient;
        this.actor = actor;
        this.card = card;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }


}