package tz.business.eCard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.Card;
import tz.business.eCard.models.UserAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findFirstByUuid(String uuid);

    void deleteCardsByUuidAndUserUuid(String uuid, String userUuid);

    Optional<Card> findFirstByUserUuidAndDeletedFalse(UserAccount account);

    Page<Card> findAllByOrganizationAndDeletedFalse(String orgUuid, Pageable pageable);

    Page<Card> findAllByDeletedFalse(Pageable pageable);

    Page<Card> findAllByDeletedFalseAndPublishCardTrue(Pageable pageable);

    Page<Card> findAllByTitleAndDeletedFalse(String title, Pageable pageable);

    Optional<Card> findFirstByUserUuid(UserAccount userUuid);

    Page<Card> findAllByActiveTrueAndDeletedFalse(Pageable pageable);

    Long countAllByUserUuid(UserAccount userUuid);
    Long countAllByUserIdAndDeletedFalse(UserAccount userId);

    Long countAllByDeletedFalse();

    Page<Card> findAllByUserUuidAndDeletedFalse(String userUuid, Pageable pageable);

    //PROXIMITY SEARCH QUERIES
    /**
     * Find all published cards that have coordinates
     */
    List<Card> findByPublishCardTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndDeletedFalse();

    /**
     * Find published cards within a certain distance using native SQL
     * This is more efficient for large datasets
     */
    @Query(value = """
        SELECT c.* FROM cards c 
        WHERE c.publish_card = true 
        AND c.latitude IS NOT NULL 
        AND c.longitude IS NOT NULL 
        AND c.deleted = false
        AND (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(CAST(c.latitude AS DECIMAL(10,8)))) * 
                cos(radians(CAST(c.longitude AS DECIMAL(11,8))) - radians(:longitude)) + 
                sin(radians(:latitude)) * sin(radians(CAST(c.latitude AS DECIMAL(10,8))))
            ) * 1000
        ) <= :maxDistanceMeters
        """, nativeQuery = true)

    List<Card> findNearbyPublishedCards(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("maxDistanceMeters") double maxDistanceMeters
    );

    /**
     * Find published cards by organization with coordinates
     */
    List<Card> findByOrganizationAndPublishCardTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndDeletedFalse(String organization);
}