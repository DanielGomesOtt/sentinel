package com.sentinel.sentinel.exceptions;

public class IncidentAlreadyClosedException extends RuntimeException {
    public IncidentAlreadyClosedException(String message) {
        super(message);
    }
}
