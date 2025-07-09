package tz.business.eCard.userDetailService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tz.business.eCard.models.Role;
import tz.business.eCard.models.Account;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final Long id;
    private final String username;
    @JsonIgnore
    private final String password;
    @Getter
    private final String phoneNumber;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
//    @Getter
//    private String email;
    @Getter
    private  final String uuid;
    @Getter
    private final String userType;
    @Enumerated(EnumType.STRING)
    private Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username , String firstName, String lastName , String phoneNumber , String uuid , String password, Role userType, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.uuid = uuid;
        this.password = password;
        this.userType = String.valueOf(userType);
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Account account){
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(account.getUserType()));
        return new UserDetailsImpl(
                account.getId(),
                account.getUserName(),
                account.getFirstName(),
                account.getLastName(),
                account.getPhoneNumber(),
                account.getUuid(),
                account.getPassword(),
                Role.valueOf(account.getUserType()),
                grantedAuthorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
