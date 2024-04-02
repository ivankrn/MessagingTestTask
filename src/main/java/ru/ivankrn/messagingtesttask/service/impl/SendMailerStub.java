package ru.ivankrn.messagingtesttask.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ivankrn.messagingtesttask.database.model.EmailAddress;
import ru.ivankrn.messagingtesttask.database.model.EmailContent;
import ru.ivankrn.messagingtesttask.service.SendMailer;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class SendMailerStub implements SendMailer {

    @Override
    public void sendMail(EmailAddress toAddress, EmailContent messageBody) throws TimeoutException {
        if(shouldThrowTimeout()) {
            sleep();

            throw new TimeoutException("Timeout!");
        }

        if(shouldSleep()) {
            sleep();
        }

        // ok.
        log.info("Message sent to {}, body {}.", toAddress, messageBody);
    }

    @SneakyThrows
    private static void sleep() {
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));
    }

    private static boolean shouldSleep() {
        return new Random().nextInt(10) == 1;
    }

    private static boolean shouldThrowTimeout() {
        return new Random().nextInt(10) == 1;
    }

}
