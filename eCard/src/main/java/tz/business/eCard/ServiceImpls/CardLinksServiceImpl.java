package tz.business.eCard.ServiceImpls;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.CardLinksDto;
import tz.business.eCard.models.CardLinks;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardLinksRepository;
import tz.business.eCard.services.CardLinksService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardLinksServiceImpl implements CardLinksService {
    @Autowired
    private CardLinksRepository cardLinksRepository;
    @Autowired
    private LoggedUser loggedUser;


    @Override
    public Response<CardLinks> createLink(CardLinksDto cardsLinkDto) {
        try {
            UserAccount userAccount = loggedUser.getUserAccount();

            CardLinks cardLinks = new CardLinks();
            if (userAccount == null) {
                return new Response<>(true, ResponseCode.UNAUTHORIZED, "Unauthorized");
            }

            if(cardLinks.getUserUuid() == null || cardLinks.getUserUuid().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Null User UUID");
            }
            if(cardLinks.getUuid() == null || cardLinks.getUuid().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Null Card UUID");
            }

            if(cardLinks.getUrl() == null || cardLinks.getUrl().isEmpty()) {
                cardLinks.setUrl("");
            }
            if (cardLinks.getQrCodeUrl() == null || cardLinks.getQrCodeUrl().isEmpty()) {
                cardLinks.setQrCodeUrl("");
            }
            if(!cardLinks.getUuid().isBlank() && !Objects.equals(cardLinks.getUuid(), cardLinks.getUserUuid())){
                Optional<Cards> linksOptional;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Page<CardLinks> getAllLinks(Pageable pageable) {
        return null;
    }

    @Override
    public Page<CardLinks> getCardsByUserId(String userId, Pageable pageable) {
        try {
            return cardLinksRepository.findALlByUserUuid(userId, pageable);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

}
