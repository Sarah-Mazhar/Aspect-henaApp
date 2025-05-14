package com.example.hena.security;

import com.example.hena.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
//    even if the database has an admin user, Spring Security doesn't know how to validate that login.
//so a custom UserDetailsService so Spring Security can authenticate users using the User entity from the database.

//    This class wraps User entity from the database to implement the UserDetails interface,
//    which Spring Security uses to work with authenticated users.
//    It provides the necessary methods (like getAuthorities()) for Spring Security to understand the user's roles and privileges.

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // The following can be customized as needed
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
