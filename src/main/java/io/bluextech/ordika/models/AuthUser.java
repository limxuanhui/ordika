package io.bluextech.ordika.models;
/* Created by limxuanhui on 23/8/24 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthUser implements UserDetails {

    private final User user;
    private String accessToken;
    private String refreshToken;

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // Below methods are unused
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
