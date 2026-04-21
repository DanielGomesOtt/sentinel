package com.sentinel.sentinel.infra.exceptions;

import com.sentinel.sentinel.exceptions.*;
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

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleOrganizationNotFoundException(OrganizationNotFoundException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleUserNotFoundException(UserNotFoundException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IncidentNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleIncidentNotFoundException(IncidentNotFoundException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<RestErrorMessage> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IncidentAlreadyClosedException.class)
    public ResponseEntity<RestErrorMessage> handleIncidentAlreadyClosedException(IncidentAlreadyClosedException ex) {
        RestErrorMessage error = new RestErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
