package ru.ivankrn.messagingtesttask.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ivankrn.messagingtesttask.controller.error.NotFoundException;
import ru.ivankrn.messagingtesttask.database.model.ApprovalRequest;
import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.MessageId;
import ru.ivankrn.messagingtesttask.database.repository.ApprovalRequestRepository;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.MessageQueue;

import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ApprovalQueueStub implements MessageQueue<ApprovalResultDTO> {

    public final ApprovalRequestRepository approvalRequestRepository;

    @Override
    public Message<ApprovalResultDTO> poll(MessageId messageId) {
        Optional<ApprovalRequest> approvalRequest = approvalRequestRepository.findById(messageId);
        if (approvalRequest.isPresent()) {
            ApprovalResultDTO approvalResultDTO = new ApprovalResultDTO(
                    approvalRequest.get().getMessageId(),
                    approvalRequest.get().getUser().getUsername(),
                    approvalRequest.get().getUser().getEmail(),
                    shouldApprove()
            );
            approvalRequestRepository.deleteById(messageId);
            return new Message<>(messageId, approvalResultDTO);
        }
        throw new NotFoundException("Message not found");
    }

    private static boolean shouldApprove() {
        return new Random().nextInt(2) == 1;
    }

}
