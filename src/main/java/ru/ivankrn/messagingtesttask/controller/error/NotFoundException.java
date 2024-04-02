package ru.ivankrn.messagingtesttask.controller.error;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
        super(msg);
    }

}
