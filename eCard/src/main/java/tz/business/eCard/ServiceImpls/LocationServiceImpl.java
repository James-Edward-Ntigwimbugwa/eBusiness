package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.LocationDto;
import tz.business.eCard.models.Location;
import tz.business.eCard.repositories.LocationRepository;
import tz.business.eCard.services.LocationService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    @Override
    public Response<Location> save(LocationDto locationDto) {
        try{
            Location location = new Location();
            if(locationDto.getUserId()==null){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT , "User id is null");
            }
            location.setUserId(locationDto.getUserId());

            location.setLatitude(locationDto.getLatitude());
            location.setLongitude(locationDto.getLongitude());
            Coordinate coordinate = new Coordinate(locationDto.getLongitude(), locationDto.getLatitude());

            location.setLocation(geometryFactory.createPoint(coordinate));
            location.setCreated(LocalDateTime.now());
            return new Response<>(false, ResponseCode.SUCCESS , "Location Added Successfully",  locationRepository.save(location));

        }catch (Exception e){
            log.error("An error occurred {}" ,e.getMessage());
        }
        return new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred");
    }

    @Override
    public List<Location> getUserLocations(UUID userId) {
        return locationRepository.findByUserId(userId);
    }
}
