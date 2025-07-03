package tz.business.eCard.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.business.eCard.dtos.NearbyCardInfo;
import tz.business.eCard.models.DeviceProximity;
import tz.business.eCard.models.ProximitySearchRequest;
import tz.business.eCard.services.DeviceProximityService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/device-proximity")
@CrossOrigin(origins = "*")

public class DeviceProximityController {

    @Autowired
    private DeviceProximityService deviceProximityService;

    /**
     * Find nearby cards based on users distance
     * @param request The proximity Search request with coordinates
     * @return List of nearby devices within the specified range
     */

    @PostMapping("/search-with-json-body")
    public ResponseEntity<Response<NearbyCardInfo>> findNearbyDevices(
            @Valid @RequestBody ProximitySearchRequest request){

        try{
            log.info("Searching for nearby devices at lat{} , long{}", request.getLatitude(), request.getLongitude());
            List<NearbyCardInfo> nearbyDevices = deviceProximityService.findNearbyCardsWithDetails(
                    request.getUserUuid() ,
                    request.getLatitude(),
                    request.getLongitude()
            );

            if(nearbyDevices.isEmpty()){
                return ResponseEntity.ok(new Response<>(false , ResponseCode.SUCCESS, "No nearby devices found" , nearbyDevices));
            }

            return  ResponseEntity.ok(new Response<>(false , ResponseCode.SUCCESS, "Nearby devices found" , nearbyDevices));
        }catch (Exception e){
            log.error("An error occurred while searching for nearby devices: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred while searching nearby devices"));
        }
    }

    /**
     * Get nearby devices using query parameters (alternative endpoint)
     * @param userId The user ID
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @return List of nearby devices
     */
    @GetMapping("/search-with-request-params")
    public ResponseEntity<Response<DeviceProximity>> findNearbyDevices(
            @RequestParam("userId") UUID userId,
            @RequestParam("latitude") @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
            @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90") Double latitude,
            @RequestParam("longitude") @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
            @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180") Double longitude)
    {
        try{
            log.info("Searching for nearby devices at lat{} , long{}", latitude, longitude);
            List<DeviceProximity> nearbyDevices = deviceProximityService.findNearbyDevices(userId, latitude, longitude);
            if(nearbyDevices.isEmpty()){
                return ResponseEntity.ok(new Response<>(false , ResponseCode.SUCCESS, "No nearby devices found" , nearbyDevices));
            }
            return  ResponseEntity.ok(new Response<>(false , ResponseCode.SUCCESS, "Nearby devices found" , nearbyDevices));
        }catch (Exception e){
            log.error("An error occurred while searching for nearby devices: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred while searching nearby devices"));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Response<DeviceProximity>> saveDeviceProximity(@Valid @RequestBody DeviceProximity deviceProximity){
        try{
            log.info("Saving device proximity {}", deviceProximity);
            Response<DeviceProximity> response = deviceProximityService.saveDeviceProximity(deviceProximity);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error("An error occurred while saving device proximity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred while saving device proximity"));
        }
    }

}
