package com.example.hena.security;

import com.example.hena.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetails interface,
 * allowing integration of your domain User entity into Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor to wrap your application-specific User entity.
     * @param user the domain User object
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ============================
    //  Authorities (Roles)
    // ============================

    /**
     * Returns the role of the user as a GrantedAuthority.
     * Spring Security requires roles to be prefixed with "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // ============================
    //  Credentials
    // ============================

    /**
     * Returns the user's hashed password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the user's unique username (used for login).
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // ============================
    //  Account Status Flags
    // ============================

    /**
     * Whether the account is non-expired.
     * Always true in this basic implementation.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Whether the account is not locked.
     * Always true unless locking logic is added.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Whether the credentials (password) are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Whether the account is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
