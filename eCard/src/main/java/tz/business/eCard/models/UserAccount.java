package tz.business.eCard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_account", indexes = @Index(name = "idx_lastname_firstname" , columnList = "lastName , firstName"))
@SQLDelete(sql = "UPDATE user_account SET deleted = true WHERE id=?" , check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class UserAccount extends BaseEntity{

    @Column(name = "first_name" , nullable = false)
    private String firstName;

    @Column(name = "second_name" , nullable = false)
    private String secondName;

    @Column(name = "last_name" , nullable = false)
    private String lastName;

    @Column(name = "full_name" , nullable = false)
    private String fullName ;

    @Column(name="username" , nullable = false)
    private String userName;

    @Column(name = "company_name" , nullable = false)
    private String companyName;

    @Column(name = "email" , nullable = false , unique = true)
    private String email;

    @Pattern(regexp = "(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", message = "Please enter valid phone number eg. 255766040293")
    @Column(name = "phone_number" , unique = true)
    private String phoneNumber;

    @Column(name = "user_type")
    private String userType;

    @Column(name="otp_sent")
    private String oneTimePassword;

    @Column(name = "biography")
    private String biography;

    @Column(name = "publish_bio")
    private boolean publishBio;

    @JsonIgnore
    private  String password;

    @JsonIgnore
    @Basic(optional = true)
    @Column(name = "token_created_at")
    private LocalDateTime tokenCreatedAt  = LocalDateTime.now();

    @JsonIgnore
    @Basic(optional = true)
    @Column(name = "last_login")
    private LocalDateTime lastLogin ;

    @JsonIgnore
    @Column(name = "remember_token")
    @Basic(optional = true)
    private String rememberToken;

    @JsonIgnore
    private LocalDateTime lastOtpSent;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @JsonIgnore
    private  int loginAttempts = 0;

    @Column(name = "job_title")
    private String jobTitle;

    @JsonIgnore
    private  LocalDateTime lastLoginTime;

    @JsonIgnore
    private  String refreshToken;

    @JsonIgnore
    private  LocalDateTime refreshTokenTime;

    // For CardGroup owner relationship
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference("card-group-owner")
    private List<CardGroup> groups;

    // For Cards user relationship (if present)
    @OneToMany(mappedBy = "user")
    @JsonManagedReference("cards-user")
    private List<Cards> userCards;


    private boolean accountLocked = false;
    private  boolean accountExpired = false;
    private boolean credentialsExpired = false;
    private  boolean enabled = false;
    private  boolean accountLockedByUser = false;

}
