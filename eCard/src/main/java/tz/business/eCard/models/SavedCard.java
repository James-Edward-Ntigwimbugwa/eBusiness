package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_cards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavedCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user; // The user who saved the card

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card; // The card that was saved

    @Column(name = "saved_date")
    private LocalDateTime savedDate;

    public SavedCard(UserAccount user, Card card) {
        this.user = user;
        this.card = card;
    }
}