package ru.ivankrn.messagingtesttask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.MessageId;
import ru.ivankrn.messagingtesttask.service.MessageQueue;
import ru.ivankrn.messagingtesttask.service.MessageQueueManager;
import ru.ivankrn.messagingtesttask.service.MessagingService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class MessagingServiceStub implements MessagingService {

    private final MessageQueueManager messageQueueManager;

    @Override
    public <T> MessageId send(Message<T> msg) {
        return new MessageId(UUID.randomUUID());
    }

    @Override
    public <T> Message<T> receive(MessageId messageId, Class<T> messageType) throws TimeoutException {
        if (shouldThrowTimeout()) {
            sleep();

            throw new TimeoutException("Timeout!");
        }

        if (shouldSleep()) {
            sleep();
        }

        // Разделил очереди по типам, поскольку глобальная очередь на все сообщения была бы неудобна. В случае же
        // разделения, можно брать только интересующие сообщения (потенциально брать можно было по темам, топикам, и
        // так далее).
        // Кроме того, разделение по типам позволяет реализовать поддержку кастомных конвертеров сообщений. Благодаря
        // этому, можно было бы гибко подбирать конвертеры сообщений под конкретную задачу.
        MessageQueue<T> messageQueue = messageQueueManager.findForMessageType(messageType);
        return messageQueue.poll(messageId);
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
