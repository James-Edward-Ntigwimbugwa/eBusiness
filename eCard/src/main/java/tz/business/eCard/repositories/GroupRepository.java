package tz.business.eCard.repositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.UserAccount;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<CardGroup, Long> {
    Optional<CardGroup> findFirstByUuid(String name);
    Optional<CardGroup> findFirstByGroupName(String groupName);
    Page<CardGroup> getAllByDeletedFalseOrderByGroupNameAsc(String groupName, Pageable pageable);
    Page<CardGroup> getAllByOwnerOrderByGroupNameAsc(UserAccount owner, Pageable pageable);
}
