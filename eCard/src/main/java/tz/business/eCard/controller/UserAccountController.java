package tz.business.eCard.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tz.business.eCard.dtos.BioDto;
import tz.business.eCard.dtos.UserAccountDto;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.services.UserAccountService;
import tz.business.eCard.utils.Response;

import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/users/")
public class UserAccountController {

    @Autowired(required = false)
    private UserAccountService userAccountService;

    @PostMapping("/profile-photo-upload/{uuid}")
    public  ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") @NotNull MultipartFile file, @PathVariable String uuid){
        Response<Map<String, Object>> uploadProfilePhoto = userAccountService.createUpdateProfilePhoto(uuid , file);
        return  ResponseEntity.ok().body(uploadProfilePhoto);
    }

    @PostMapping("/create-update")
    public ResponseEntity<?> createUser(@RequestBody UserAccountDto userAccountDto) {
        Response<UserAccount> createUser = userAccountService.createUpdateUserAccount(userAccountDto);
        return ResponseEntity.ok().body(createUser);
    }

    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<?> deleteUser(@PathVariable String uuid) {
        Response<UserAccount> deleteUser = userAccountService.deleteUserAccount(uuid);

        return ResponseEntity.ok().body(deleteUser);
    }

    @GetMapping(path = "get-user/{uuid}")
    public ResponseEntity<?> getUserByUuid(@PathVariable String uuid){
        Response<UserAccount> getUser = userAccountService.getUserByUuid(uuid);
        return ResponseEntity.ok().body(getUser);
    }

    @GetMapping(path = "/get-all")
    public ResponseEntity<?> getAllUsers(@RequestParam(value = "page", defaultValue = "0")Integer page,
                                         @RequestParam(value = "size", defaultValue =  "25")Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<UserAccount> all = userAccountService.getAllUserAccounts(pageable);
        return ResponseEntity.ok()
                .body(all);
    }

    @GetMapping(path = "/get-customers")
    public ResponseEntity<?> getCustomers(@RequestParam(value = "page" , defaultValue = "0")Integer page,
                                          @RequestParam(value = "size" , defaultValue = "25")Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserAccount> customers = userAccountService.getCustomers(pageable);
        return  ResponseEntity.ok().body(customers);

    }

    @GetMapping(path = "/get-officials")
    public  ResponseEntity<?> getOfficials(@RequestParam(value = "page" , defaultValue = "0")Integer page ,
                                           @RequestParam(value = "size" , defaultValue = "25")Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserAccount> officials = userAccountService.getOfficials(pageable);
        return  ResponseEntity.ok().body(officials);
    }

    @GetMapping(path = "/delete-user/{uuid}")
    public ResponseEntity<?> deleteUserByUuid(@PathVariable String uuid){
        Response<UserAccount> deleteUser = userAccountService.deleteUserAccount(uuid);
        return ResponseEntity.ok().body(deleteUser);
    }

    @PutMapping("/update-bio")
    public ResponseEntity<?> updateUserBio(@RequestBody BioDto bioDto){
        Response<UserAccount> response = userAccountService.updateBio(bioDto);

        return ResponseEntity.ok().body(response);
    }

}
