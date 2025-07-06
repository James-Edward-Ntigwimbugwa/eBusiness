package tz.business.eCard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.CardDto;
import tz.business.eCard.dtos.GroupCardsDto;
import tz.business.eCard.dtos.MyCardDto;
import tz.business.eCard.models.Card;
import tz.business.eCard.services.CardService;
import tz.business.eCard.utils.Response;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping(path = "/all")
    public ResponseEntity<?> getCards(@RequestParam(defaultValue = "0" , value = "page")Integer page,
                                      @RequestParam(defaultValue = "25" , value = "size")Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardDto> all = cardService.getAllCards(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

    @GetMapping(path = "/all-active")
    public ResponseEntity<?> getCardsActive(@RequestParam(defaultValue = "25" , value = "page")Integer page,
                                            @RequestParam(defaultValue = "10" , value = "size")Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> allActiveCards = cardService.getAllActiveCards(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(allActiveCards);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> createCard(@RequestBody CardDto cardDto) {
        log.info("endpoint hit");
        Response<Card> response = cardService.createCard(cardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> deleteCard(@RequestParam String cardUuid) {
        Response<Boolean> response = cardService.deleteCard(cardUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCard(@RequestBody CardDto cardDto) {
        Response<Card> updatedCard = cardService.updateCard(cardDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCard);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchCard(@RequestParam("title") String keyword,
                                        @RequestParam(defaultValue = "0" , value = "page")Integer page,
                                        @RequestParam(defaultValue = "25" , value = "size") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Card> response = cardService.searchCardsByTitle(keyword , pageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/published-active")
    public ResponseEntity<?> publishedActiveCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> response = cardService.getAllPublicActiveCards(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/card-by-uuid")
    public ResponseEntity<?> getCardByUuid(@RequestParam String uuid) {
        Response<Card> response = cardService.getCardByUuid(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user-cards")
    public ResponseEntity<?> getUserCardsByUuid(@RequestParam String uuid) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> response = cardService.getCardsByUserUuid(uuid, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/save-card")
    public ResponseEntity<?> saveCard(@RequestBody MyCardDto myCardDto) {
        Response<Card> response = cardService.saveCard(myCardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/group-cards")
    public ResponseEntity<?> groupCards(@RequestBody GroupCardsDto groupCardsDto) {
        Response<Card> response = cardService.groupCards(groupCardsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
