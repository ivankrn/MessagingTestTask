package ru.ivankrn.messagingtesttask.controller.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ErrorResponse {

    private int code;
    private String status;
    private String message;
    private Object data;
    private OffsetDateTime timestamp;

    public ErrorResponse() {
        timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this();
        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
    }

    public ErrorResponse(HttpStatus httpStatus, String message, Object data) {
        this(httpStatus, message);
        this.data = data;
    }

}
