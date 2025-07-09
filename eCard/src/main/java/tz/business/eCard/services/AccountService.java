package tz.business.eCard.services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tz.business.eCard.dtos.BioDto;
import tz.business.eCard.dtos.AccountDto;
import tz.business.eCard.models.Account;
import tz.business.eCard.utils.Response;

import java.util.Map;

@Service
public interface AccountService {
    Response<Account> createUpdateUserAccount(AccountDto accountDto);

    Response<Account> deleteUserAccount(String uuid);

    Response<Account> getUserByUuid(String uuid);

    Page<Account> getAllUserAccounts(Pageable pageable);

    Page<Account> getOfficials(Pageable pageable);

    Page<Account> getCustomers(Pageable pageable);

    Page<Account> getVendors(Pageable pageable);

    Response<Account> updateBio(BioDto bioDto);

    Response<Map<String , Object>> createUpdateProfilePhoto(String uuid, MultipartFile file);

}
