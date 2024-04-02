package ru.ivankrn.messagingtesttask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ivankrn.messagingtesttask.controller.error.NotFoundException;
import ru.ivankrn.messagingtesttask.database.model.EmailAddress;
import ru.ivankrn.messagingtesttask.database.model.EmailContent;
import ru.ivankrn.messagingtesttask.database.model.Message;
import ru.ivankrn.messagingtesttask.database.model.User;
import ru.ivankrn.messagingtesttask.database.repository.UserRepository;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.EmailFormatter;
import ru.ivankrn.messagingtesttask.service.MessageListener;
import ru.ivankrn.messagingtesttask.service.SendMailer;

import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalMessageListener implements MessageListener<ApprovalResultDTO> {

    private final UserRepository userRepository;
    private final SendMailer sendMailer;
    private final EmailFormatter<ApprovalResultDTO> emailFormatter;

    @Override
    public void handleMessage(Message<ApprovalResultDTO> incomingMessage) {
        boolean isApproved = incomingMessage.getContent().isApproved();
        String userEmail = incomingMessage.getContent().email();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (isApproved) {
            user.setEnabled(true);
            userRepository.save(user);
        }
        EmailAddress toAddress = new EmailAddress(userEmail);
        EmailContent emailContent = emailFormatter.format(incomingMessage.getContent());
        try {
            sendMailer.sendMail(toAddress, emailContent);
        } catch (TimeoutException e) {
            // Наверно в реальности стоило бы перекладывать сообщение в очередь необработанных (или с ошибками)
            // сообщений, потому что в текущей реализации необработанные сообщения удаляются
            log.error("Can't send email, {}", e.getMessage());
        }
    }

}
