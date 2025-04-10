package tz.business.eCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.Location;

import java.util.List;
import java.util.UUID;
@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByUserId(UUID userId);
}
