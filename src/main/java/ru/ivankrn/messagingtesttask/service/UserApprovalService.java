package ru.ivankrn.messagingtesttask.service;

import ru.ivankrn.messagingtesttask.database.model.User;

/**
 * Сервис для подачи заявок на одобрение регистрации пользователей.
 */
public interface UserApprovalService {

    /**
     * Подает заявку на одобрение регистрации пользователя.
     *
     * @param user пользователь
     */
    void submitForApproval(User user);

}
