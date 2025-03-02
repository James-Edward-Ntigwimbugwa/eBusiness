package tz.business.eCard.services;

import tz.business.eCard.dtos.LocationDto;
import tz.business.eCard.models.Location;
import tz.business.eCard.utils.Response;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    Response<Location> save(LocationDto locationDto);
    List<Location> getUserLocations(UUID userId);
}
