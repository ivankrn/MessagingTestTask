package ru.ivankrn.messagingtesttask.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message<T> {

    private MessageId id;
    private T content;

}
