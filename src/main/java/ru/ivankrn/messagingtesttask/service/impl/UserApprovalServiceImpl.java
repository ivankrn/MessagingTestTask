package ru.ivankrn.messagingtesttask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ivankrn.messagingtesttask.database.model.ApprovalRequest;
import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.MessageId;
import ru.ivankrn.messagingtesttask.database.model.User;
import ru.ivankrn.messagingtesttask.database.repository.ApprovalRequestRepository;
import ru.ivankrn.messagingtesttask.dto.ApprovalRequestDTO;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.MessageListener;
import ru.ivankrn.messagingtesttask.service.MessagingService;
import ru.ivankrn.messagingtesttask.service.UserApprovalService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

// Можно было бы обобщить решение: сделать компоненты, ответственные за сбор сообщений из шины, которые при получении
// сообщения делегировали бы его обработку лисенерам.
@Service
@RequiredArgsConstructor
@Slf4j
public class UserApprovalServiceImpl implements UserApprovalService {

    private final MessagingService messagingService;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final MessageListener<ApprovalResultDTO> messageListener;
    private final Executor executor = Executors.newFixedThreadPool(8);
    // Использую конкуррентный set, т.к. добавление задач будет происходить в нескольких потоках
    private final Set<MessageId> messagesInWork = ConcurrentHashMap.newKeySet();

    @Override
    public void submitForApproval(User user) {
        UUID messageUUID = UUID.randomUUID();
        MessageId messageId = new MessageId(messageUUID);
        ApprovalRequestDTO approvalRequestDTO = new ApprovalRequestDTO(user.getEmail());
        Message<ApprovalRequestDTO> approveRequest = new Message<>(messageId, approvalRequestDTO);
        MessageId approveRequestCorrelationId = messagingService.send(approveRequest);
        ApprovalRequest approvalRequest = new ApprovalRequest(approveRequestCorrelationId, user);
        // Скорее всего не очень хороший подход, поскольку при подаче заявки сохранение происходит в этом сервисе, а
        // при чтении из шины происходит удаление уже в другом месте.
        approvalRequestRepository.save(approvalRequest);
    }

    // Возможно стоило бы использовать для обновления ScheduledExecutorService вместо @Scheduled, чтобы иметь
    // возможность более гибко настроить распределение по потокам и прочие политики. Сейчас используется @Scheduled для
    // отслеживания состояний заявок регистрации и простой ExecutorService для обработки сообщений.
    @Scheduled(fixedRateString = "${messaging.rateInMs}")
    public void refreshApprovals() {
        List<ApprovalRequest> approvalRequests = approvalRequestRepository.findAll();
        approvalRequests
                .parallelStream()
                .forEach(approvalRequest -> {
                    MessageId messageId = approvalRequest.getMessageId();
                    if (!messagesInWork.contains(messageId)) {
                        messagesInWork.add(messageId);
                        Runnable task = () -> {
                            try {
                                Message<ApprovalResultDTO> approvalResult =
                                        messagingService.receive(messageId, ApprovalResultDTO.class);
                                messageListener.handleMessage(approvalResult);
                            } catch (TimeoutException e) {
                                // Наверно в реальности стоило бы перекладывать сообщение в очередь необработанных (или с ошибками)
                                // сообщений, потому что в текущей реализации необработанные сообщения удаляются
                                log.error(e.getMessage());
                            }
                            messagesInWork.remove(messageId);
                        };
                        executor.execute(task);
                    }
                });
    }

}
