package tz.business.eCard.services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tz.business.eCard.dtos.BioDto;
import tz.business.eCard.dtos.UserAccountDto;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.utils.Response;

import java.util.Map;

@Service
public interface UserAccountService {
    Response<UserAccount> createUpdateUserAccount(UserAccountDto userAccountDto);

    Response<UserAccount> deleteUserAccount(String uuid);

    Response<UserAccount> getUserByUuid(String uuid);

    Page<UserAccount> getAllUserAccounts(Pageable pageable);

    Page<UserAccount> getOfficials(Pageable pageable);

    Page<UserAccount> getCustomers(Pageable pageable);

    Page<UserAccount> getVendors(Pageable pageable);

    Response<UserAccount> updateBio(BioDto bioDto);

    Response<Map<String , Object>> createUpdateProfilePhoto(String uuid, MultipartFile file);

}
