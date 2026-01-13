package com.sentinel.sentinel.infra.exceptions;

import com.sentinel.sentinel.exceptions.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<RestErrorMessage> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
