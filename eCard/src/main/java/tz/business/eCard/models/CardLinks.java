package tz.business.eCard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "card_links")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE  card_links SET  deleted=true WHERE  id = ?" , check = ResultCheckStyle.COUNT)
public class CardLinks extends BaseEntity {

    @Column(name = "url")
    private String url;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @ManyToOne
    @JoinColumn(name = "cards_uuid" , referencedColumnName = "uuid")
    @JsonBackReference
    private Card cards;

    @Column(name = "user_uuid")
    private String userUuid;

}
