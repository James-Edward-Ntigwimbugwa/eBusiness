package tz.business.eCard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
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
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonBackReference("card-group-owner") // Unique name
    private UserAccount owner;

    @OneToMany(mappedBy = "group")
    @JsonManagedReference("cards-group")
    private List<Cards> cards = new ArrayList<>();
}
