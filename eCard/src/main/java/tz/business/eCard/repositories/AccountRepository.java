package tz.business.eCard.repositories;

import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findFirstByPhoneNumber(@Pattern(regexp = "(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", message = "Please enter valid phone number eg. 255766040293") String phoneNumber);
    Optional<Account> findFirstByUuid(String uuid);
    Optional<Account> findFirstByEmail(String email);
    Page<Account> findAll(Pageable pageable);
    Optional<Account> findFirstByRefreshToken(String refreshToken);
    Page<Account> findALlByDeletedFalse(Pageable pageable);
    Page<Account> findAllByUserTypeNot(String role, Pageable pageable);
    Optional<Account> findByOneTimePassword(String oneTimePassword);
    Page<Account> findAllByUserTypeAndDeletedFalse(String userType, Pageable pageable);
    Long countAllByActiveTrue();

    Optional<Account> findFirstByUserName(String userName);

    // Case-insensitive username search
    @Query("SELECT a FROM Account a WHERE LOWER(a.userName) = LOWER(:userName) AND a.deleted = false")
    Optional<Account> findByUserNameIgnoreCase(@Param("userName") String userName);

    // Case-insensitive phone number search
    @Query("SELECT a FROM Account a WHERE a.phoneNumber = :phoneNumber AND a.deleted = false")
    Optional<Account> findByPhoneNumberAndNotDeleted(@Param("phoneNumber") String phoneNumber);

    Optional<Account> findFirstByEmailAndDeletedFalse(String email);
}