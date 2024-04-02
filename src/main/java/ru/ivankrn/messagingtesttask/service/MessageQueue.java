package ru.ivankrn.messagingtesttask.service;

import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.MessageId;

/**
 * Очередь сообщений.
 *
 * @param <T> тип сообщений
 */
public interface MessageQueue<T> {

    /**
     * Извлекает сообщение с указанным id.
     *
     * @param messageId id сообщения
     * @return сообщение
     */
    Message<T> poll(MessageId messageId);

}
