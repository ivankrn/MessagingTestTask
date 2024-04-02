package ru.ivankrn.messagingtesttask.controller.error;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }

}
