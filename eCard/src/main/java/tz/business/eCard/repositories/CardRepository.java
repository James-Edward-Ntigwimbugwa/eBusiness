package tz.business.eCard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.UserAccount;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Cards, Long> {

    Optional<Cards> findFirstByUuid(String uuid);

    Optional<Cards> findFirstByUserUuidAndDeletedFalse(UserAccount account);

//    Page<Cards> findAllByUserUuidAndDeletedFalse(UserAccount userAccount, Pageable pageable);

    Page<Cards> findAllByOrganizationAndDeletedFalse(String orgUuid, Pageable pageable);

    Page<Cards> findAllByDeletedFalse(Pageable pageable);

    Page<Cards> findAllByDeletedFalseAndPublishCardTrue(Pageable pageable);

    Page<Cards> findAllByTitleAndDeletedFalse(String title, Pageable pageable);

    Optional<Cards> findFirstByUserUuid(UserAccount userUuid);
    Optional<Cards> findCardsByAddress(String address);


    Long  countAllByUserUuid(UserAccount userUuid);

    Long countAllByDeletedFalse();

    Page<Cards> findAllByUserUuidAndDeletedFalse(UserAccount userUuid, Pageable pageable);

}
