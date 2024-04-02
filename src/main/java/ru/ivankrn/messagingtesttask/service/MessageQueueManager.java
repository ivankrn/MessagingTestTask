package ru.ivankrn.messagingtesttask.service;

/**
 * Менеджер очередей сообщений.
 */
public interface MessageQueueManager {

    /**
     * Находит очередь, соответствующую типу сообщения.
     *
     * @param messageType тип сообщения
     * @param <T>
     * @return очередь сообщений
     */
    <T> MessageQueue<T> findForMessageType(Class<T> messageType);

}
