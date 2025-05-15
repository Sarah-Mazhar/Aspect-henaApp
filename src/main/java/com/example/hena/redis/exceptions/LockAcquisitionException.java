package com.example.hena.redis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class LockAcquisitionException extends RuntimeException {
    public LockAcquisitionException(String message) {
        super(message);
    }
}
