package tz.business.eCard.models;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.smartcardio.Card;
import java.io.Serializable;

@Entity
@Where(clause = "deleted = false")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyCards extends BaseEntity implements Serializable {

    @Column(name = "favourites")
    private String favourites;

    @ManyToOne
    @JoinColumn(name = "card_id" , referencedColumnName = "id")
    private Cards card;

    @ManyToOne
    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    private UserAccount userId;

}
