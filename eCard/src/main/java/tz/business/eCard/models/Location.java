package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "userId" , nullable = false)
    private UUID userId;

    @Column(name="latitude" , nullable = false)
    private double latitude;

    @Column(name="longitude" , nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime created;

}
