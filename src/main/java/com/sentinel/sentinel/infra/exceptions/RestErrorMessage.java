package com.sentinel.sentinel.infra.exceptions;

public class RestErrorMessage {

    private String message;

    public RestErrorMessage (String errorMessage) {
        this.message = errorMessage;
    }

    public String getMessage() {
        return message;
    }
}
