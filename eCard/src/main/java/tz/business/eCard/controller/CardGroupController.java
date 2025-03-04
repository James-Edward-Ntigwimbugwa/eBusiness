package tz.business.eCard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.GroupDto;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.services.GroupService;
import tz.business.eCard.utils.Response;

@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
public class CardGroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/create-update")
    private ResponseEntity<?> createUpdateGroup(@RequestBody GroupDto groupDto) {
        log.info("Endpoint hit with createUpdateGroup groupDto: {}", groupDto);
        Response<CardGroup> response =  groupService.createUpdateGroup(groupDto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<?> deleteCardGroup(@PathVariable String uuid) {
        Response<CardGroup> response = groupService.deleteGroup(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get-my-groups")
    public ResponseEntity<?> getMyGroups(@RequestParam(defaultValue = "0" , value = "page")int page,
                                         @RequestParam(defaultValue = "25" , value = "25")int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardGroup> response =  groupService.getAllUserGroups(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/v1/group/{uuid}")
    public ResponseEntity<?> getGroup(@PathVariable String uuid) {
        Response<CardGroup> response = groupService.getGroup(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

