package tz.business.eCard.repositories;

import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.business.eCard.models.UserAccount;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {

    Optional<UserAccount> findFirstByPhoneNumber(@Pattern(regexp = "(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", message = "Please enter valid phone number eg. 255766040293") String phoneNumber);
    Optional<UserAccount> findFirstByUuid(String uuid);
    Optional<UserAccount> findFirstByEmail(String email);
    Page<UserAccount> findAll(Pageable pageable);
    Optional<UserAccount> findFirstByRefreshToken(String refreshToken);
    Page<UserAccount> findALlByDeletedFalse(Pageable pageable);
    Page<UserAccount> findAllByUserTypeNot(String role, Pageable pageable);
    Optional<UserAccount> findByOneTimePassword(String oneTimePassword);
    Page<UserAccount> findAllByUserTypeAndDeletedFalse(String userType, Pageable pageable);
    Long countAllByActiveTrue();

    Optional<UserAccount> findFirstByUserName(String username);
    Optional<UserAccount> findFirstByEmailAndDeletedFalse(String email);
}
