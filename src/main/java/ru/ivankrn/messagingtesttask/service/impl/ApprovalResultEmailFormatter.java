package ru.ivankrn.messagingtesttask.service.impl;

import org.springframework.stereotype.Service;
import ru.ivankrn.messagingtesttask.database.model.EmailContent;
import ru.ivankrn.messagingtesttask.dto.ApprovalResultDTO;
import ru.ivankrn.messagingtesttask.service.EmailFormatter;

@Service
public class ApprovalResultEmailFormatter implements EmailFormatter<ApprovalResultDTO> {

    @Override
    public EmailContent format(ApprovalResultDTO approvalResult) {
        EmailContent emailContent = new EmailContent();
        StringBuilder sb = new StringBuilder();
        sb.append("Hello, %s! ".formatted(approvalResult.login()));
        if (approvalResult.isApproved()) {
            sb.append("Your account has been successfully verified.");
        } else {
            sb.append("Sorry, your account has not been verified.");
        }
        String emailBody = sb.toString();
        emailContent.setBody(emailBody);
        return emailContent;
    }

}
