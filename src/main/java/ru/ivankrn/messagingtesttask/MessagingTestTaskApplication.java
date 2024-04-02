package ru.ivankrn.messagingtesttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessagingTestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagingTestTaskApplication.class, args);
    }

}
