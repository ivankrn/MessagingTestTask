package ru.ivankrn.messagingtesttask.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "approval_request")
@Getter
@Setter
@NoArgsConstructor
public class ApprovalRequest {

    @EmbeddedId
    private MessageId messageId;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    private boolean isApproved;

    public ApprovalRequest(MessageId messageId, User user) {
        this.messageId = messageId;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalRequest other)) {
            return false;
        }
        return Objects.equals(getMessageId(), other.getMessageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageId());
    }


}
