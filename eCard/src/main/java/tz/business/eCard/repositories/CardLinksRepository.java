package tz.business.eCard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.CardLinks;

import java.util.Optional;

@Repository
public interface CardLinksRepository extends JpaRepository<CardLinks, Long> {
    Optional<CardLinks> findFirstByUuid(String uuid);
    Optional<CardLinks> findFirstByUrl(String url);
    Page<CardLinks> findALlByUserUuid(String uuid , Pageable pageable);
    Optional<CardLinks> findFirstByQrCodeUrl(String qrCodeUrl);
    Page<CardLinks> findAllByQrCodeUrl(String qrCodeUrl, Pageable pageable);

}
