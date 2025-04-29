package tz.business.eCard.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.Point;
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

    @Column(name = "latitude" , nullable = false)
    private Double latitude;

    @Column(name = "longitude" , nullable = false)
    private Double longitude;

    @Column(columnDefinition = "GEOMETRY(Point,4326)")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Point location;

    @Column(nullable = false)
    private LocalDateTime created;

}
