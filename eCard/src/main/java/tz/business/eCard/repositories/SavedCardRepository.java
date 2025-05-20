package tz.business.eCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.SavedCard;

import java.util.List;

@Repository
public interface SavedCardRepository extends JpaRepository<SavedCard, Long> {
    List<SavedCard> findByCardId(Long cardId);
    List<SavedCard> findByUserId(Long userId);
    boolean existsByUserIdAndCardId(Long userId, Long cardId);
    void deleteByUserIdAndCardId(Long userId, Long cardId);
}