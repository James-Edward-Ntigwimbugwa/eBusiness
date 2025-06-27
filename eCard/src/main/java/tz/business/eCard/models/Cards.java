package tz.business.eCard.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cards", indexes = {@Index(name = "idx_email" , columnList = "email"),
                @Index(name = "idx_organization" , columnList = "organization")})
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE cards SET deleted = true WHERE id = ?" , check = ResultCheckStyle.COUNT )

public class Cards extends BaseEntity {
    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "organization" , nullable = false)
    private String organization;

    @Column(name = "publishCard")
    private boolean publishCard ;

    @Column(name = "card_logo")
    private String cardLogo;

    @Column(name = "profile_photo" , nullable = true)
    private String profilePhoto;

    @Column(name = "latitude")
    private String latitude;

    @Column(name="longitude")
    private  String longitude;

    @Column(name="selected_address")
    private  String selected_address;

    @Column(name = "card_description" , nullable = false)
    private String cardDescription;

    @Column(name = "phone_number" , nullable = false)
    private String phoneNumber;

    @Column(name = "department")
    private String department;

    @Column(name="email")
    private String email;

    @Column(name = "linkedIn")
    private String linkedIn;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "font_color")
    private String fontColor;

    @Column(name="text_position")
    private String textPosition;

    @Column(name="logo_position")
    private String logoPosition;

    @Column(name = "font_style")
    private  String fontStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    @JsonBackReference("cards-user") // Unique name
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "group_uuid", referencedColumnName = "id")
    @JsonBackReference("cards-group") // Unique name
    private CardGroup group;
}
