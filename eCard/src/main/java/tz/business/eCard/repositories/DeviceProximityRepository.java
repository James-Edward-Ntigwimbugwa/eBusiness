package tz.business.eCard.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.DeviceProximity;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceProximityRepository extends JpaRepository<DeviceProximity, UUID> {
    List<DeviceProximity> findByUserId(UUID userId);
}
