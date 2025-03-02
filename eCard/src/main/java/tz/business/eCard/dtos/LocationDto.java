package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private UUID uuid;
    private  UUID userId;
    private double latitude;
    private double longitude;
    private LocalDateTime dateTime;
}
