package ru.ivankrn.messagingtesttask.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.Role;
import ru.ivankrn.messagingtesttask.database.model.User;
import ru.ivankrn.messagingtesttask.database.repository.ApprovalRequestRepository;
import ru.ivankrn.messagingtesttask.dto.ApprovalRequestDTO;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.MessageListener;
import ru.ivankrn.messagingtesttask.service.MessagingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserApprovalServiceImplTest {

    private MessagingService messagingService;
    private ApprovalRequestRepository approvalRequestRepository;
    private MessageListener<ApprovalResultDTO> messageListener;
    private UserApprovalServiceImpl userApprovalService;

    @BeforeEach
    public void setUp() {
        this.messagingService = Mockito.mock();
        this.approvalRequestRepository = Mockito.mock();
        this.messageListener = Mockito.mock();
        this.userApprovalService =
                new UserApprovalServiceImpl(messagingService, approvalRequestRepository, messageListener);
    }

    @Test
    public void givenUser_whenSubmitForApproval() {
        String username = "test";
        String userEmail = "test@mail.com";
        User user = new User(
                1L,
                username,
                "12345",
                userEmail,
                "Краснов Илья Сергеевич",
                Role.USER,
                false);

        verify(messagingService, never()).send(any());
        verify(approvalRequestRepository, never()).save(any());

        userApprovalService.submitForApproval(user);

        verify(messagingService).send(argThat((Message<ApprovalRequestDTO> message) ->
                message.getContent().email().equals(userEmail)
        ));
        verify(approvalRequestRepository).save(argThat(approvalRequest -> approvalRequest.getUser().equals(user)));
    }

}