package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import tz.business.eCard.models.DeviceProximity;
import tz.business.eCard.models.Location;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.DeviceProximityRepository;
import tz.business.eCard.repositories.LocationRepository;
import tz.business.eCard.services.DeviceProximityService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DeviceProximityServiceImp implements DeviceProximityService {
    private static final double MAX_DISTANCE = 5000.00; // in meters

    @Autowired
    private DeviceProximityRepository deviceProximityRepository;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private LocationRepository locationRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public List<DeviceProximity> findNearbyDevices(UUID userId, double latitude, double longitude) {
        try {
            UserAccount user = loggedUser.getUserAccount();

            if (user == null) {
                log.error("Unauthorized access attempt");
                return new ArrayList<>();
            }

            // Create a point from the provided coordinates
            Point userPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));

            // Find locations within MAX_DISTANCE meters
            List<Location> nearbyLocations = getLocationsNear(longitude, latitude, MAX_DISTANCE);

            List<DeviceProximity> nearbyDevices = new ArrayList<>();

            for (Location location : nearbyLocations) {
                // Calculate precise distance for storing in the proximity record
                double distance = calculateDistance(latitude, longitude,
                        location.getLocation().getY(),
                        location.getLocation().getX());

                nearbyDevices.add(
                        new DeviceProximity(
                                UUID.randomUUID(),
                                userId,
                                location.getUuid(),
                                distance,
                                LocalDateTime.now()
                        )
                );
            }

            return nearbyDevices;
        } catch (Exception e) {
            log.error("An exception occurred while finding nearby devices: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Use the spatial query to find locations within a given distance
     */
    public List<Location> getLocationsNear(double longitude, double latitude, double distance) {
        try {
            return locationRepository.findWithinDistance(longitude, latitude, distance);
        } catch (Exception e) {
            log.error("Error finding locations near coordinates: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculate the precise distance between two coordinates using the haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Convert to meters
    }

    @Override
    public Response<DeviceProximity> saveDeviceProximity(DeviceProximity deviceProximity) {
        try {
            return new Response<>(false, ResponseCode.CREATED, "Proximity Saved", deviceProximityRepository.save(deviceProximity));
        } catch (Exception e) {
            log.error("An error occurred while saving device proximity: {}", e.getMessage());
        }
        return new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred");
    }
}