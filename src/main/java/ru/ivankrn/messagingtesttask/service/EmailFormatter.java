package ru.ivankrn.messagingtesttask.service;

import ru.ivankrn.messagingtesttask.database.model.EmailContent;

/**
 * Форматировщик объектов в их email представления.
 *
 * @param <T> тип объектов
 */
public interface EmailFormatter<T> {

    /**
     * Возвращает представление объекта в виде {@link EmailContent содержимого email}.
     *
     * @param object объект
     * @return отформатированное представление объекта
     */
    EmailContent format(T object);

}
