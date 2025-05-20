package tz.business.eCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.Location;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    /**
     * Find locations within a specific distance from a point
     * @param longitude The longitude coordinate
     * @param latitude The latitude coordinate
     * @param distance The distance in meters
     * @return List of locations within the specified distance
     */
    @Query(value = "SELECT l.* FROM location l " +
            "WHERE ST_DWithin(" +
            "   l.location, " +
            "   ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), " +
            "   :distance)" +
            "ORDER BY ST_Distance(l.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))",
            nativeQuery = true)
    List<Location> findWithinDistance(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("distance") double distance);

    /**
     * Find all locations associated with a specific user ID
     * @param userId The UUID of the user
     * @return List of locations for the specified user
     */
    List<Location> findByUserId(UUID userId);
}