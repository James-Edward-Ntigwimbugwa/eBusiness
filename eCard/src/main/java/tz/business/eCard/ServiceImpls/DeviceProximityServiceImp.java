
package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import tz.business.eCard.dtos.NearbyCardInfo;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.DeviceProximity;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.repositories.DeviceProximityRepository;
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
    private CardRepository cardRepository; // Updated to use CardRepository

    @Autowired
    private LoggedUser loggedUser;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public List<DeviceProximity> findNearbyDevices(UUID userId, double latitude, double longitude) {
        try {
            UserAccount user = loggedUser.getUserAccount();

            if (user == null) {
                log.error("Unauthorized access attempt");
                return new ArrayList<>();
            }

            // Find published cards with coordinates within MAX_DISTANCE
            List<Cards> nearbyCards = getNearbyCards(latitude, longitude, MAX_DISTANCE);

            List<DeviceProximity> nearbyDevices = new ArrayList<>();

            for (Cards card : nearbyCards) {
                try {
                    // Parse card coordinates (they're stored as strings)
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());

                    // Calculate precise distance
                    double distance = calculateDistance(latitude, longitude, cardLat, cardLng);

                    // Only include if within max distance
                    if (distance <= MAX_DISTANCE) {
                        nearbyDevices.add(new DeviceProximity(
                                UUID.randomUUID(),
                                userId,
                                card.getUuid(),
                                distance,
                                LocalDateTime.now()
                        ));
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid coordinates for card {}: lat={}, lng={}",
                            card.getUuid(), card.getLatitude(), card.getLongitude());
                }
            }

            log.info("Found {} nearby devices for user {} at coordinates ({}, {})",
                    nearbyDevices.size(), userId, latitude, longitude);

            return nearbyDevices;
        } catch (Exception e) {
            log.error("An exception occurred while finding nearby devices: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get nearby cards that are published and have valid coordinates
     */
    private List<Cards> getNearbyCards(double userLat, double userLng, double maxDistance) {
        try {
            // Get all published cards that have coordinates
            List<Cards> publishedCards = cardRepository.findByPublishCardTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndDeletedFalse();

            List<Cards> nearbyCards = new ArrayList<>();

            for (Cards card : publishedCards) {
                try {
                    // Parse coordinates
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());

                    // Calculate distance
                    double distance = calculateDistance(userLat, userLng, cardLat, cardLng);

                    // Include if within max distance
                    if (distance <= maxDistance) {
                        nearbyCards.add(card);
                    }
                } catch (NumberFormatException e) {
                    log.debug("Skipping card {} due to invalid coordinates", card.getUuid());
                }
            }

            return nearbyCards;
        } catch (Exception e) {
            log.error("Error finding nearby cards: {}", e.getMessage());
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
            DeviceProximity saved = deviceProximityRepository.save(deviceProximity);
            return new Response<>(false, ResponseCode.CREATED, "Proximity Saved", saved);
        } catch (Exception e) {
            log.error("An error occurred while saving device proximity: {}", e.getMessage());
            return new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    /**
     * Get nearby devices with card details for better response
     */
    public List<NearbyCardInfo> findNearbyCardsWithDetails(UUID userId, double latitude, double longitude) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if (user == null) {
                log.error("Unauthorized access attempt");
                return new ArrayList<>();
            }

            List<Cards> nearbyCards = getNearbyCards(latitude, longitude, MAX_DISTANCE);
            List<NearbyCardInfo> result = new ArrayList<>();

            for (Cards card : nearbyCards) {
                try {
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());
                    double distance = calculateDistance(latitude, longitude, cardLat, cardLng);

                    result.add(new NearbyCardInfo(
                            card.getUuid(),
                            card.getTitle(),
                            card.getOrganization(),
                            card.getEmail(),
                            card.getPhoneNumber(),
                            card.getCardLogo(),
                            card.getProfilePhoto(),
                            distance,
                            cardLat,
                            cardLng
                    ));
                } catch (NumberFormatException e) {
                    log.warn("Invalid coordinates for card {}", card.getUuid());
                }
            }

            return result;
        } catch (Exception e) {
            log.error("Error finding nearby cards with details: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}