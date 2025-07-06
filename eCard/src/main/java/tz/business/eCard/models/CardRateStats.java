package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents  card rates by people and level of trusteeship of the business organization
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "card_rate_stats")
public class CardRateStats  {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_uuid")
    private Card card;

    @OneToOne
    @JoinColumn(name = "user_uuid")
    private UserAccount account;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "positive_rates_count")
    private Long positiveCount;

    @Column(name="negative_rates_count")
    private Long negativeCounts;

    @Column(name="neutral_rate_counts")
    private Long neutralCounts;

}
