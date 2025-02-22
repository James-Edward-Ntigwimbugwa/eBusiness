package tz.business.eCard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.CardLinksDto;
import tz.business.eCard.models.CardLinks;
import tz.business.eCard.services.CardLinksService;
import tz.business.eCard.utils.Response;

@RestController
@RequestMapping(path = "api/v1/links")
public class CardLinksController {

    @Autowired
    private CardLinksService cardLinksService;

    @PostMapping("/new-link")
    private ResponseEntity<?> createNewLink(@RequestBody CardLinksDto cardLinksDto){

        Response<CardLinks> response = cardLinksService.createLink(cardLinksDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/card-links")
    private ResponseEntity<?> getAllLinks(){
        return  null;
    }

    @GetMapping("/user-links/{uuid}")
    private ResponseEntity<?> getUserLinks(@PathVariable(value = "uuid") String uuid ,
                                           @RequestParam(value = "page" , defaultValue = "0") Integer page,
                                           @RequestParam(value = "size" , defaultValue = "25")Integer size){
            Pageable pageable = PageRequest.of(page, size);
            Page<CardLinks> response = cardLinksService.getCardsByUserId(uuid , pageable);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
