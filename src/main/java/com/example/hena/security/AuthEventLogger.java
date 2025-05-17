package com.example.hena.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthEventLogger {

    private static final Logger log = LoggerFactory.getLogger(AuthEventLogger.class);

    @EventListener
    public void handleLoginSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info(" LOGIN SUCCESS: {}", username);
    }

    @EventListener
    public void handleLoginFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        log.warn(" LOGIN FAILED: {}", username);
    }
}
