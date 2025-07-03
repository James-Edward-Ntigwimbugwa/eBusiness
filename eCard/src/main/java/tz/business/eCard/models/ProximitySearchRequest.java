package tz.business.eCard.models;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProximitySearchRequest {
    @NotNull(message = "User Uuid is required")
    private UUID userUuid;

    @NotNull(message = "Latitude is required")
    @DecimalMax(value = "90" , message = "Latitude must be between -90 and 90")
    @DecimalMin(value = "-90" , message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMax(value = "180" , message = "Latitude must be between -180 and 180")
    @DecimalMin(value = "-180" , message = "Latitude must be between -180 and 180")
    private Double longitude;

}
