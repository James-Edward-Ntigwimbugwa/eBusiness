package tz.business.eCard.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tz.business.eCard.dtos.CardLinksDto;
import tz.business.eCard.models.CardLinks;
import tz.business.eCard.utils.Response;

import javax.smartcardio.Card;

public interface CardLinksService {
    Response<CardLinks> createLink(CardLinksDto cardsLinkDto) ;
    Page<CardLinks> getAllLinks(Pageable pageable);
    Page<CardLinks> getCardsByUserId(String userId, Pageable pageable);
    Page<CardLinks> getCardLinkByUrl(String url, Pageable pageable);
    Page<CardLinks> getCardLinkByQRCode(String qrCode, Pageable pageable);
    Page<CardLinks> deleteCardLinkByUrl(String url, Pageable pageable);

}
