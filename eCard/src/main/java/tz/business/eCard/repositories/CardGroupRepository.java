package tz.business.eCard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.Account;
import java.util.Optional;

@Repository
public interface CardGroupRepository extends JpaRepository<CardGroup, Long> {
    Optional<CardGroup> findFirstByUuid(String uuid);
    Optional<CardGroup> findFirstByGroupName(String groupName);
    Page<CardGroup> getAllByDeletedFalseOrderByGroupNameAsc(Pageable pageable);
//    Page<CardGroup> getAllByOwnerOrderByGroupNameAsc(Pageable pageable);
    Page<CardGroup> getAllByOwnerOrderByGroupNameAsc(Account owner, Pageable pageable);
}
