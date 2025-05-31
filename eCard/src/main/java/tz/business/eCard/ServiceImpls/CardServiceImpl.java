package tz.business.eCard.ServiceImpls;
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
import tz.business.eCard.models.Cards;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardGroupRepository;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.services.CardService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;
import java.util.Collections;
import java.util.Objects;
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
    public Response<Cards> createCard(CardDto cardDto) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            log.info("user {}" , user);
            Cards cards = new Cards();

            if(user == null) {
                return  new Response<>(true, ResponseCode.UNAUTHORIZED, "Unauthorized");
            }

            if(cardDto.getOrganization()==null || cardDto.getOrganization().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Organization is null");
            }

            if (cardDto.getTitle() == null || cardDto.getTitle().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Title is null");
            }

            if(cardDto.getAddress()==null || cardDto.getAddress().isBlank()) {
                return  new Response<>(true, ResponseCode.NULL_ARGUMENT, "Address is null");
            }

            // Set default values for optional fields
            cards.setLinkedIn(cardDto.getLinkedin() != null ? cardDto.getLinkedin() : "");
            cards.setEmail(cardDto.getEmail() != null ? cardDto.getEmail() : "");
            cards.setWebsiteUrl(cardDto.getWebsiteUrl() != null ? cardDto.getWebsiteUrl() : "");
            cards.setProfilePhoto(cardDto.getProfilePhoto() != null ? cardDto.getProfilePhoto() : "");

            // Phone number validation - FIX: Check before validation and don't return early in the else block
            if(cardDto.getPhoneNumber()==null || cardDto.getPhoneNumber().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Phone number is empty");
            } else if(authService.isValidPhoneNumber(cardDto.getPhoneNumber())) {
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid phone number");
            }

            // Set the phone number
            cards.setPhoneNumber(cardDto.getPhoneNumber());

            // Set other required fields
            cards.setTitle(cardDto.getTitle());
            cards.setOrganization(cardDto.getOrganization());
            cards.setAddress(cardDto.getAddress());
            cards.setCardDescription(cardDto.getCardDescription());

            // Set optional fields with appropriate defaults
            cards.setBackgroundColor(cardDto.getBackgroundColor() != null ? cardDto.getBackgroundColor() : "");
            cards.setFontColor(cardDto.getFontColor() != null ? cardDto.getFontColor() : "");
            cards.setDepartment(cardDto.getDepartment() != null ? cardDto.getDepartment() : "");

            // Set the card status
            cards.setPublishCard(cardDto.isPublishCard());
            cards.setUser(user);
            cards.setDeleted(false);
            cards.setActive(true);

            // Save the card and return the response
            Cards cards1 = cardRepository.save(cards);
            return new Response<>(false, ResponseCode.SUCCESS, "Card saved successfully", cards1);

        } catch (Exception e){
            log.error(e.getMessage());
            return new Response<>(true, ResponseCode.BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Cards> updateCard(CardDto cardDto) {
        try{
            UserAccount user = loggedUser.getUserAccount();

            if(cardDto.getTitle()==null || cardDto.getTitle().isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Title is null");
            }

            if(cardDto.getAddress()==null || cardDto.getAddress().isBlank()) {
                return  new Response<>(true, ResponseCode.NULL_ARGUMENT, "Address is null");
            }

            if(cardDto.getOrganization()==null || cardDto.getOrganization().isBlank()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Organization is null");
            }

            if(user == null) {
                log.warn("UNAUTHORIZED ACCESS WITH CREDENTIALS {}" , cardDto.getEmail() + " " +cardDto.getPhoneNumber());
                return  new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }

            Optional<Cards> cardsOptional = cardRepository.findFirstByUuid(cardDto.getUuid());
            if(cardsOptional.isPresent()) {
                Cards cards = cardsOptional.get();
                cards.setTitle(cardDto.getTitle());
                cards.setAddress(cardDto.getAddress());
                cards.setOrganization(cardDto.getOrganization());
                cards.setEmail(cardDto.getEmail() != null ? cardDto.getEmail() : "");

                if(cardDto.getPhoneNumber() == null || cardDto.getPhoneNumber().isBlank()) {
                    return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Phone number is empty");
                } else if(authService.isValidPhoneNumber(cardDto.getPhoneNumber())) {
                    return new Response<>(true, ResponseCode.BAD_REQUEST, "Invalid phone number");
                }

                cards.setPhoneNumber(cardDto.getPhoneNumber());
                cards.setProfilePhoto(cardDto.getProfilePhoto() != null ? cardDto.getProfilePhoto() : "");
                cards.setCardDescription(cardDto.getCardDescription());
                cards.setBackgroundColor(cardDto.getBackgroundColor() != null ? cardDto.getBackgroundColor() : "");
                cards.setFontColor(cardDto.getFontColor() != null ? cardDto.getFontColor() : "");
                cards.setDepartment(cardDto.getDepartment() != null ? cardDto.getDepartment() : "");
                cards.setLinkedIn(cardDto.getLinkedin() != null ? cardDto.getLinkedin() : "");
                cards.setWebsiteUrl(cardDto.getWebsiteUrl() != null ? cardDto.getWebsiteUrl() : "");

                Cards updatedCard = cardRepository.save(cards);
                return new Response<>(false, ResponseCode.SUCCESS, "Card updated successfully", updatedCard);
            } else {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return new Response<>(true, ResponseCode.BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteCard(String cardId) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return;
            }
            if(cardId == null) {
                return;
            }
            Optional<Cards> cardsOptional = cardRepository.findFirstByUuid(cardId);
            if(cardsOptional.isPresent()) {
                Cards cards = cardsOptional.get();
                cards.setDeleted(true);
                cards.setActive(false);
                cardRepository.save(cards);
                cardRepository.delete(cards);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public Page<Cards> getAllCards(Pageable pageable) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByDeletedFalse(pageable);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Cards> searchCardsByTitle(String title, Pageable pageable) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return null;
            }
            return cardRepository.findAllByTitleAndDeletedFalse(title, pageable);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Cards> getAllActiveCards(Pageable pageable) {

        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByActiveTrueAndDeletedFalse(pageable);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Cards> getAllPublicActiveCards(Pageable pageable) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new PageImpl<>(Collections.emptyList());
            }
            return cardRepository.findAllByDeletedFalseAndPublishCardTrue(pageable);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new PageImpl<>(Collections.emptyList());    }

    @Override
    public Response<Cards> getCardByUuid(String uuid) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }
            Optional<Cards> card = cardRepository.findFirstByUuid(uuid);
            return card.map(cards -> new Response<>(true, ResponseCode.SUCCESS, cards)).orElseGet(() -> new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found"));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return new Response<>(true , ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Page<Cards> getCardsByUserUuid(String uuid, Pageable pageable) {
        UserAccount user = loggedUser.getUserAccount();
        if(user==null) {
            return null;
        }
        return cardRepository.findAllByUserUuidAndDeletedFalse(uuid , pageable);
    }

    @Override
    public Response<Cards> saveCard(MyCardDto myCardDto) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Unauthorized access");
            }
            Optional<Cards> optionalCards = cardRepository.findFirstByUuid(myCardDto.getUuid());
            if(optionalCards.isPresent()) {
                Cards cards = optionalCards.get();
                cardRepository.save(cards);
                return new Response<>(false, ResponseCode.SUCCESS, "Card saved successfully", cards);
            } else {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Card not found");
            }

        }catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }

    @Override
    public Response<Cards> groupCards(GroupCardsDto groupCardsDto) {
        try{
            Optional<Cards> optionalCards = cardRepository.findFirstByUuid(groupCardsDto.getCardUuid());
            Optional<CardGroup> cardGroupOptional  =  cardGroupRepository.findFirstByUuid(groupCardsDto.getCardUuid());

            if(optionalCards.isPresent() && cardGroupOptional.isPresent()) {
                Cards cards = optionalCards.get();
                CardGroup group = cardGroupOptional.get();
                cards.setGroup(group);
                group.getCards().add(cards);
                Cards cards1 = cardRepository.save(cards);
                cardGroupRepository.save(group);
                return new Response<>(false , ResponseCode.SUCCESS, "Cards group saved successfully" , cards1);
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST, "Unknown error occurred");
    }
}