package ru.ivankrn.messagingtesttask.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ivankrn.messagingtesttask.controller.error.NotFoundException;
import ru.ivankrn.messagingtesttask.database.model.*;
import ru.ivankrn.messagingtesttask.database.repository.UserRepository;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.MessageListener;
import ru.ivankrn.messagingtesttask.service.SendMailer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalMessageListenerTest {

    private UserRepository userRepository;
    private SendMailer sendMailer;
    private final ApprovalResultEmailFormatter emailFormatter = new ApprovalResultEmailFormatter();
    private MessageListener<ApprovalResultDTO> approvalMessageListener;

    @BeforeEach
    public void setUp() {
        this.userRepository = Mockito.mock();
        this.sendMailer = Mockito.mock();
        this.approvalMessageListener = new ApprovalMessageListener(userRepository, sendMailer, emailFormatter);
    }

    @Test
    public void givenApprovedFromExternalSystem_WhenHandleMessage_thenEnableUser() throws TimeoutException {
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
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        MessageId messageId = new MessageId(UUID.randomUUID());
        ApprovalResultDTO approvalResultDTO = new ApprovalResultDTO(messageId, username, userEmail, true);
        Message<ApprovalResultDTO> message = new Message<>(messageId, approvalResultDTO);
        EmailAddress toAddress = new EmailAddress(userEmail);
        EmailContent messageBody = emailFormatter.format(approvalResultDTO);

        assertFalse(user.isEnabled());
        verify(userRepository, never()).save(argThat((User u) -> u.getId().equals(user.getId())));
        verify(sendMailer, never()).sendMail(toAddress, messageBody);

        approvalMessageListener.handleMessage(message);

        assertTrue(user.isEnabled());
        verify(userRepository).save(argThat((User u) -> u.getId().equals(user.getId()) && u.isEnabled()));
        verify(sendMailer).sendMail(toAddress, messageBody);
    }

    @Test
    public void givenNotApprovedFromExternalSystem_WhenHandleMessage_thenDoNotEnableUser() throws TimeoutException {
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
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        MessageId messageId = new MessageId(UUID.randomUUID());
        ApprovalResultDTO approvalResultDTO = new ApprovalResultDTO(messageId, username, userEmail, false);
        Message<ApprovalResultDTO> message = new Message<>(messageId, approvalResultDTO);
        EmailAddress toAddress = new EmailAddress(userEmail);
        EmailContent messageBody = emailFormatter.format(approvalResultDTO);

        assertFalse(user.isEnabled());
        verify(userRepository, never()).save(argThat((User u) -> u.getId().equals(user.getId())));
        verify(sendMailer, never()).sendMail(toAddress, messageBody);

        approvalMessageListener.handleMessage(message);

        assertFalse(user.isEnabled());
        verify(userRepository, never()).save(argThat((User u) -> u.getId().equals(user.getId()) && u.isEnabled()));
        verify(sendMailer).sendMail(toAddress, messageBody);
    }

    @Test
    public void givenUserNotFound_whenHandleMessage_thenThrowNotFoundException() {
        String username = "test";
        String userEmail = "test@mail.com";
        MessageId messageId = new MessageId(UUID.randomUUID());
        ApprovalResultDTO approvalResultDTO = new ApprovalResultDTO(messageId, username, userEmail, false);
        Message<ApprovalResultDTO> message = new Message<>(messageId, approvalResultDTO);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        Exception exception =
                assertThrows(NotFoundException.class, () -> approvalMessageListener.handleMessage(message));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

}