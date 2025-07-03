package tz.business.eCard.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.SaveCardRequest;
import tz.business.eCard.models.SavedCard;
import tz.business.eCard.services.SavedCardService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/saved-cards")
public class SavedCardController {
    @Autowired
    private SavedCardService savedCardService;

    @PostMapping("/save-card")
    public ResponseEntity<SavedCard> saveCard(@RequestBody SaveCardRequest request) {
        log.info("Endpoint hit with request: {}", request);
        SavedCard savedCard = savedCardService.saveCard(request.getUserId(), request.getCardId());
        return ResponseEntity.ok(savedCard);
    }

    @DeleteMapping("/delete-saved-card")
    public ResponseEntity<Void> unsaveCard(@RequestParam Long userId, @RequestParam Long cardId) {
        savedCardService.unsaveCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedCard>> getUserSavedCards(@PathVariable Long userId) {
        log.info("Method executed in saved card controller with userId: {}", userId);
        List<SavedCard> savedCards = savedCardService.findByUserId(userId);
        return ResponseEntity.ok(savedCards);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<SavedCard>> getCardSaves(@PathVariable Long cardId) {
        List<SavedCard> savedCards = savedCardService.findByCardId(cardId);
        return ResponseEntity.ok(savedCards);
    }
}