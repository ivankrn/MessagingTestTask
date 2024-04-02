package ru.ivankrn.messagingtesttask.service.impl;

import org.springframework.stereotype.Component;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.MessageQueue;
import ru.ivankrn.messagingtesttask.service.MessageQueueManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageQueueManagerImpl implements MessageQueueManager {

    private final Map<Class<?>, MessageQueue<?>> queues = new ConcurrentHashMap<>();

    public MessageQueueManagerImpl(MessageQueue<ApprovalResultDTO> messageQueue) {
        queues.put(ApprovalResultDTO.class, messageQueue);
    }

    @Override
    public <T> MessageQueue<T> findForMessageType(Class<T> messageType) {
        return (MessageQueue<T>) queues.get(messageType);
    }

}
