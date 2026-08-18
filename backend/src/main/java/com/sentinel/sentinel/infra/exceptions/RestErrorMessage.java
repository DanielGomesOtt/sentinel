package com.sentinel.sentinel.infra.exceptions;

public class RestErrorMessage {

    private String error;

    public RestErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
