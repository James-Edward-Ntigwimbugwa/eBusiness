package tz.business.eCard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "card_groups")
public class CardGroup extends BaseEntity implements Serializable {
    @Column(name = "group_name")
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "owner_id" , referencedColumnName = "id")
    @JsonBackReference
    private UserAccount owner;

    @OneToMany
    @JoinColumn(name = "cards" , referencedColumnName = "id")
    private List<Cards> cards;

}
