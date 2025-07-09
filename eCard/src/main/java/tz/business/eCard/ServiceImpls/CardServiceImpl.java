package tz.business.eCard.ServiceImpls;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.CardDto;
import tz.business.eCard.dtos.GroupCardsDto;
import tz.business.eCard.dtos.MyCardDto;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.Card;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.CardGroupRepository;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.services.CardService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardGroupRepository cardGroupRepository;
    @Autowired
    private LoggedUser loggedUser;

    @Override
    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
    }

    @Override
    public Response<Card> createCard(CardDto cardDto) {
        try {
            Account user = loggedUser.getUserAccount();
            log.info("user {}", user);
            Card cards = new Card();

            if (user == null) {
                return new Response<>(true, ResponseCode.UNAUTHORIZED, "Unauthorized");
            }

            if (cardDto.getOrganization() == null || cardDto.getOrganization().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Organization is null");
            }

            if (cardDto.getTitle() == null || cardDto.getTitle().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Title is null");
            }

            if (cardDto.getPhoneNumber() == null || cardDto.getPhoneNumber().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Phone number is empty");
            }

            if (authService.isValidPhoneNumber(cardDto.getPhoneNumber())) {
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid phone number");
            }

            cards.setTitle(cardDto.getTitle());
            cards.setOrganization(cardDto.getOrganization());
            cards.setPhoneNumber(cardDto.getPhoneNumber());
            cards.setTextPosition(cardDto.getTextPosition());
            cards.setLogoPosition(cardDto.getLogoPosition());
            cards.setFontStyle(cardDto.getFontStyle());

            cards.setLinkedIn(cardDto.getLinkedin() != null && !cardDto.getLinkedin().isBlank() ?
                    cardDto.getLinkedin() : "");

            cards.setLatitude(cardDto.getLatitude() != null && !cardDto.getLatitude().isBlank() ?
                    cardDto.getLatitude() : "");

            cards.setLongitude(cardDto.getLongitude() != null && !cardDto.getLongitude().isBlank() ?
                    cardDto.getLongitude() : "");

            cards.setSelected_address(cardDto.getSelectedAddress() != null && !cardDto.getSelectedAddress().isBlank() ?
                    cardDto.getSelectedAddress() : "");

            cards.setEmail(cardDto.getEmail() != null && !cardDto.getEmail().isBlank() ?
                    cardDto.getEmail() : "");

            cards.setWebsiteUrl(cardDto.getWebsiteUrl() != null && !cardDto.getWebsiteUrl().isBlank() ?
                    cardDto.getWebsiteUrl() : "");

            cards.setProfilePhoto(cardDto.getProfilePhoto() != null && !cardDto.getProfilePhoto().isBlank() ?
                    cardDto.getProfilePhoto() : "");

            cards.setCardDescription(cardDto.getCardDescription() != null ?
                    cardDto.getCardDescription() : "");

            cards.setBackgroundColor(cardDto.getBackgroundColor() != null && !cardDto.getBackgroundColor().isBlank() ?
                    cardDto.getBackgroundColor() : "");

            cards.setFontColor(cardDto.getFontColor() != null && !cardDto.getFontColor().isBlank() ?
                    cardDto.getFontColor() : "");

            cards.setDepartment(cardDto.getDepartment() != null && !cardDto.getDepartment().isBlank() ?
                    cardDto.getDepartment() : "");

            cards.setPublishCard(cardDto.isPublishCard());
            cards.setUser(user);
            cards.setDeleted(false);
            cards.setActive(true);

            // Add debug logging to verify the values
            log.info("Setting latitude: {}", cardDto.getLatitude());
            log.info("Setting longitude: {}", cardDto.getLongitude());
            log.info("Setting address: {}", cardDto.getSelectedAddress());

            Card cards1 = cardRepository.save(cards);
            return new Response<>(false, ResponseCode.SUCCESS, "Card saved successfully", cards1);

        } catch (Exception e) {
            log.error("Error creating card: ", e);
            return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
        }
    }

    @Override
    public Response<Card> updateCard(CardDto cardDto) {
        try {
            Account user = loggedUser.getUserAccount();

            if (cardDto.getTitle() == null || cardDto.getTitle().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Title is null");
            }


            if (cardDto.getOrganization() == null || cardDto.getOrganization().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Organization is null");
            }

            if (user == null) {
                log.warn("UNAUTHORIZED ACCESS WITH CREDENTIALS {}", cardDto.getEmail() + " " + cardDto.getPhoneNumber());
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }

            Card cards = new Card();
            Optional<Card> cardsOptional = cardRepository.findFirstByUuid(cardDto.getUuid());
            if (cardsOptional.isPresent()) {
                cards = cardsOptional.get();
                cards.setTitle(cardDto.getTitle());
                cards.setOrganization(cardDto.getOrganization());
                cards.setEmail(cardDto.getEmail());
                cards.setPhoneNumber(cardDto.getPhoneNumber());
                cards.setProfilePhoto(cardDto.getProfilePhoto());
            } else {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    @Transactional
    public Response<Boolean> deleteCard(String cardUuid) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }
            if (cardUuid == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card UUID is null");
            }
            Optional<Card> cardsOptional = cardRepository.findFirstByUuid(cardUuid);
            if (cardsOptional.isPresent()) {
                Card card = cardsOptional.get();
                card.setDeleted(true);
                card.setActive(false);
                card.setGroup(null);
                card.setUser(null);
                card.setPublishCard(false);
                cardRepository.save(card);
                cardRepository.deleteCardsByUuidAndUserUuid(cardUuid , user.getUuid());
                cardRepository.delete(card);
                return new Response<>(false, ResponseCode.SUCCESS, "Card deleted successfully", true);
            }else {
                return new Response<>(true, ResponseCode.NOT_FOUND, "No Card With Such Uuid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
        }
    }

    @Override
    public Page<CardDto> getAllCards(Pageable pageable) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByDeletedFalse(pageable).map(this::convertToDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new PageImpl<>(Collections.emptyList());
    }

    private CardDto convertToDTO(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setUuid(card.getUuid());
//        dto.setCreatedAt(card.getCreatedAt());
//        dto.setUpdatedAt(card.getUpdatedAt());
//        dto.setCreatedBy(card.getCreatedBy());
//        dto.setDeleted(card.getDeleted());
//        dto.setActive(card.getActive());
        dto.setTitle(card.getTitle());
        dto.setOrganization(card.getOrganization());
        dto.setPublishCard(card.isPublishCard());
        dto.setCardLogo(card.getCardLogo());
        dto.setProfilePhoto(card.getProfilePhoto());
        dto.setLongitude(card.getLongitude());
        dto.setLatitude(card.getLatitude());
        dto.setLogoPosition(card.getLogoPosition());
        dto.setTextPosition(card.getTextPosition());
        dto.setFontStyle(card.getFontStyle());
        dto.setCardDescription(card.getCardDescription());
        dto.setPhoneNumber(card.getPhoneNumber());
        dto.setDepartment(card.getDepartment());
        dto.setEmail(card.getEmail());
//        dto.setLinkedIn(card.getLinkedIn());
        dto.setWebsiteUrl(card.getWebsiteUrl());
        dto.setBackgroundColor(card.getBackgroundColor());
        dto.setFontColor(card.getFontColor());
//        if (card.getUser() != null) {
//            dto.setUserUuid(card.getUser().getUuid());
//        }
//        if (card.getGroup() != null) {
//            dto.setGroupId(card.getGroup().getId());
//        }
        return dto;
    }

    @Override
    public Page<Card> searchCardsByTitle(String title, Pageable pageable) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return null;
            }
            return cardRepository.findAllByTitleAndDeletedFalse(title, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Card> getAllActiveCards(Pageable pageable) {

        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByActiveTrueAndDeletedFalse(pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Card> getAllPublicActiveCards(Pageable pageable) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByDeletedFalseAndPublishCardTrue(pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Response<Card> getCardByUuid(String uuid) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }
            Optional<Card> card = cardRepository.findFirstByUuid(uuid);
            return card.map(cards -> new Response<>(true, ResponseCode.SUCCESS, cards)).orElseGet(() -> new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found"));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Page<Card> getCardsByUserUuid(String uuid, Pageable pageable) {
        Account user = loggedUser.getUserAccount();
        if (user == null) {
            return null;
        }
        return cardRepository.findAllByUserUuidAndDeletedFalse(uuid, pageable);
    }

    @Override
    public Response<Card> saveCard(MyCardDto myCardDto) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }
            Optional<Card> optionalCards = cardRepository.findFirstByUuid(myCardDto.getUuid());
            if (optionalCards.isPresent()) {
                Card cards = optionalCards.get();
                cardRepository.save(cards);
                return new Response<>(true, ResponseCode.SUCCESS, "Card saved successfully");
            } else {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found");
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<Card> groupCards(GroupCardsDto groupCardsDto) {
        try {
            Optional<Card> optionalCards = cardRepository.findFirstByUuid(groupCardsDto.getCardUuid());
            Optional<CardGroup> cardGroupOptional = cardGroupRepository.findFirstByUuid(groupCardsDto.getCardUuid());

            if (optionalCards.isPresent() && cardGroupOptional.isPresent()) {
                Card cards = optionalCards.get();
                CardGroup group = cardGroupOptional.get();
                cards.setGroup(group);
                group.getCards().add(cards);
                Card cards1 = cardRepository.save(cards);
                cardGroupRepository.save(group);
                return new Response<>(false, ResponseCode.SUCCESS, "Cards group saved successfully", cards1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }
}
