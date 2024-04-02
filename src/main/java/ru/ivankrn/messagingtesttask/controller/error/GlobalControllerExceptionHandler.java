package ru.ivankrn.messagingtesttask.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    public static final String WRONG_USER_CREDENTIALS = "Wrong user credentials";
    public static final String USER_ACCOUNT_NOT_ACTIVATED = "User account isn't activated";
    public static final String VALIDATION_MESSAGE = "Validation error";

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException() {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new ErrorResponse(status, WRONG_USER_CREDENTIALS));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException() {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new ErrorResponse(status, USER_ACCOUNT_NOT_ACTIVATED));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> validationErrors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                validationErrors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        exception.getBindingResult().getGlobalErrors().forEach(globalError ->
                validationErrors.add(globalError.getObjectName() + ": " + globalError.getDefaultMessage()));
        return ResponseEntity.status(status).body(new ErrorResponse(status, VALIDATION_MESSAGE, validationErrors));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(status, exception.getMessage()));
    }

}
