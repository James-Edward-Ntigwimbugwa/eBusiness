package tz.business.eCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.MyCards;
import tz.business.eCard.models.Account;

@Repository
public interface MyCardRepository extends JpaRepository<MyCards, Long> {
    Long countByUserIdAndDeletedFalse(Account user);
    Long countMyCardsByUserIdAndFavouritesTrueAndDeletedFalse(Account user);
    
}
