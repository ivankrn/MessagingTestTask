# Форма регистрации с отправкой email после одобрения из внешней системы.

Дана форма регистрации в нашем приложении, в которой необходимо заполнить:
- логин,
- пароль,
- адрес электронной почты,
- ФИО.

После отправки формы, мы регистрируем данные из нее в нашей БД, а также отправляем ее для одобрения во внешней системе.
Пусть обмен с этой внешней системой будет через некое messaging решение. После одобрения или отклонения заявки, наше
приложение должно отправить сообщение на электронную почту нашему пользователю с результатом проверки.

Стек: JavaSE 8+, Spring boot 2+, dbms - h2. Для тестов предпочтение Junit/Mockito/Assertj, т.к. на проекте будут именно
они. Остальное по вкусу.

В качестве абстракции над шиной предлагаем взять такой набросок: https://pastebin.com/qWjRPuyp

Возвращать из примеров в наброске можно заглушки, дабы сэкономить время на реализацию тестового задания. Неплохо при
этом помнить, что в реальной эксплуатации любая часть нашей системы может отказать. Будем очень рады обоснованиям
принятых архитектурных решений. Комментарии в коде к ним крайне приветствуются.

## Предварительная настройка
1. Склонировать репозиторий:
`git clone https://github.com/ivankrn/MessagingTestTask`
2. Создать файл `application.yml` по пути *src/main/resources/application.yml* со следующим содержимым:
```
spring:
  application:
    name: MessagingTestTask
jwt:
  secretKey: <секретный ключ для подписи JWT токенов в виде Base64 строки>
messaging:
  rateInMs: <частота запроса сообщений из messaging решения в миллисекундах>
```
