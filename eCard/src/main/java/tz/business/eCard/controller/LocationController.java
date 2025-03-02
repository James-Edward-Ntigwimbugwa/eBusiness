package tz.business.eCard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.LocationDto;
import tz.business.eCard.models.Location;
import tz.business.eCard.services.LocationService;
import tz.business.eCard.utils.Response;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "api/v1/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<?> updateLocation(@RequestBody LocationDto locationDto) {
        log.info("locationDto: {}", locationDto);
        Response<Location> response = locationService.save(locationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<?> getLocation(@PathVariable String uuid) {
        return ResponseEntity.ok().body(locationService.getUserLocations(UUID.fromString(uuid)));
    }

}
