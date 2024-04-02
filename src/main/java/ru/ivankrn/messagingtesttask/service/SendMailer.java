package ru.ivankrn.messagingtesttask.service;

import ru.ivankrn.messagingtesttask.database.model.EmailAddress;
import ru.ivankrn.messagingtesttask.database.model.EmailContent;

import java.util.concurrent.TimeoutException;

/**
 * Ориентировочный интерфейс мейлера.
 */
public interface SendMailer {
    void sendMail (EmailAddress toAddress, EmailContent messageBody) throws TimeoutException;
}
