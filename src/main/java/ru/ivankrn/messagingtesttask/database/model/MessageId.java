package ru.ivankrn.messagingtesttask.database.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MessageId {

    private UUID uuid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageId messageId = (MessageId) o;
        return Objects.equals(uuid, messageId.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
