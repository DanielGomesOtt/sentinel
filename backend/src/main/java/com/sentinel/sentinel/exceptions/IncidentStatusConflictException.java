package com.sentinel.sentinel.exceptions;

public class IncidentStatusConflictException extends RuntimeException {
    public IncidentStatusConflictException(String message) {
        super(message);
    }
}
