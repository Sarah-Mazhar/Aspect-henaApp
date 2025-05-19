package com.example.hena.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * AuthEventLogger listens to Spring Security's authentication events
 * and logs successful and failed login attempts for auditing purposes.
 */
@Component
public class AuthEventLogger {

    private static final Logger log = LoggerFactory.getLogger(AuthEventLogger.class);

    // ============================
    //  Login Success Handler
    // ============================

    /**
     * Logs a message when a user successfully logs in.
     * Triggered by Spring Security's AuthenticationSuccessEvent.
     *
     * @param event the successful authentication event
     */
    @EventListener
    public void handleLoginSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info(" LOGIN SUCCESS: {}", username);
    }

    // ============================
    //  Login Failure Handler
    // ============================

    /**
     * Logs a message when a login attempt fails due to bad credentials.
     * Triggered by Spring Security's AuthenticationFailureBadCredentialsEvent.
     *
     * @param event the failed authentication event
     */
    @EventListener
    public void handleLoginFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        log.warn(" LOGIN FAILED: {}", username);
    }
}
