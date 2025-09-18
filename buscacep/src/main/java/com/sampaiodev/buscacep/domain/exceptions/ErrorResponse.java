package com.sampaiodev.buscacep.domain.exceptions;

import java.time.Instant;

public class ErrorResponse {
    private final String message;
    private final String details;
    private final Instant timestamp;

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public String getMessage() { return message; }
    public String getDetails() { return details; }
    public Instant getTimestamp() { return timestamp; }
}
