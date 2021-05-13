# Payment Component Documentation

## English

This is a payment component for a Java application. The purpose of this component is to handle money transfers between
logged-in users of the system. During the payment process, the amount is converted to Russian Rubles (RUB) based on the
exchange rate provided by the Central Bank of Russia (CBR). Additionally, a transaction fee is calculated depending on
the transferred amount. Finally, the notification service is called to send push notifications to the involved users.

### Usage

To use the `PaymentService`, simply call the `processPayment` method, passing in the following parameters:

- `amount`: The amount of money to be transferred.
- `currency`: The currency in which the amount is specified.
- `recipientId`: The ID of the recipient user.

The method will automatically handle the payment process, including the currency conversion, fee calculation, and
notification.

---

**Note:** This code review task is intended to be used during an interview. The candidate will be asked to review the
provided code, identify any issues or improvements, and discuss their suggestions with the interviewer.

## Русский

Данный компонент платежей предназначен для Java-приложения. Он предназначен для обработки перевода денежных средств
между зарегистрированными пользователями системы. В процессе платежа сумма конвертируется в российские рубли (RUB) по
курсу, предоставленному Центральным Банком России (ЦБР). Кроме того, рассчитывается комиссия за транзакцию в зависимости
от переведенной суммы. В конце вызывается сервис нотификаций для отправки push-уведомлений участникам операции.

### Использование

Для использования `PaymentService` достаточно вызвать метод `processPayment`, передав следующие параметры:

- `amount`: Сумма перевода.
- `currency`: Валюта, в которой указана сумма.
- `recipientId`: ID пользователя-получателя.

Метод автоматически обработает платеж, включая конвертацию валюты, расчет комиссии и отправку уведомлений.

---

**Примечание:** Это задание по проверке кода будет использоваться во время интервью. От кандидата потребуется изучить
предоставленный код, выявить возможные проблемы или улучшения и обсудить свои предложения с интервьюером.
