package ru.ivankrn.messagingtesttask.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ivankrn.messagingtesttask.database.model.ApprovalRequest;
import ru.ivankrn.messagingtesttask.database.model.MessageId;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, MessageId> {
}
