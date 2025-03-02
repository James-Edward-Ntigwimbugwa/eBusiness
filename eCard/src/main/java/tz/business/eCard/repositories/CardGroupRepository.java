package tz.business.eCard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.UserAccount;

import javax.smartcardio.Card;
import java.util.Optional;

@Repository
public interface CardGroupRepository extends JpaRepository<CardGroup, Long> {
    Optional<CardGroup> findFirstByUuid(String uuid);
    Optional<CardGroup> findFirstByGroupName(String groupName);
    Page<CardGroup> getAllByDeletedFalseOrderByGroupNameAsc(Pageable pageable);
//    Page<CardGroup> getAllByOwnerOrderByGroupNameAsc(Pageable pageable);
    Page<CardGroup> getAllByOwnerOrderByGroupNameAsc(UserAccount owner, Pageable pageable);
}
