package ru.ivankrn.messagingtesttask.dto;

import ru.ivankrn.messagingtesttask.database.model.MessageId;

public record ApprovalResultDTO(MessageId messageId, String login, String email, boolean isApproved) {
}
