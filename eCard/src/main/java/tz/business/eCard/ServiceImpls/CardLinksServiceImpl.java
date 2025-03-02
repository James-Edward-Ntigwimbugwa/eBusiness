package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.CardLinksDto;
import tz.business.eCard.models.CardLinks;
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardLinksRepository;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.services.CardLinksService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CardLinksServiceImpl implements CardLinksService {
    @Autowired
    private CardLinksRepository cardLinksRepository;
    @Autowired
    private LoggedUser loggedUser;
    @Autowired
    private CardRepository cardRepository;


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
                Optional<Cards> linksOptional = cardRepository.findFirstByUuid(cardLinks.getUuid());
                linksOptional.ifPresent(cards -> cardLinks.setUuid(cards.getUuid()));
            }

            if(!cardsLinkDto.getUrl().isBlank() && !Objects.equals(cardsLinkDto.getUrl(), cardLinks.getUrl())){
                cardLinks.setUrl(cardsLinkDto.getUrl());
            }

            if(!cardsLinkDto.getQrCodeUrl().isBlank() && !Objects.equals(cardsLinkDto.getQrCodeUrl(), cardLinks.getUrl())){
                cardLinks.setQrCodeUrl(cardsLinkDto.getQrCodeUrl());
            }
            cardLinks.setUuid(userAccount.getUuid());

            CardLinks savedCardLinks = cardLinksRepository.save(cardLinks);
            return new Response<>(false, ResponseCode.SUCCESS, "Card link added successfully",savedCardLinks);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
    @Override
    public Page<CardLinks> getAllLinks(Pageable pageable) {
        try{
            return  cardLinksRepository.findAll(pageable);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<CardLinks> getCardsByUserId(String userId, Pageable pageable) {
        try {
            return cardLinksRepository.findALlByUserUuid(userId, pageable);
        }catch (Exception e) {
            log.info("error occurred {}", e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<CardLinks> getCardLinkByUrl(String url, Pageable pageable) {
        try{
            return cardLinksRepository.findAllByQrCodeUrl(url, pageable);
        }catch (Exception e) {
            log.info("error occurred {}", e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<CardLinks> getCardLinkByQRCode(String qrCode, Pageable pageable) {
        return null;
    }

    @Override
    public Page<CardLinks> deleteCardLinkByUrl(String url, Pageable pageable) {
        return null;
    }

}
