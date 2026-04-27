# Payment Component Documentation

This is a payment component for a Java application. It handles money transfers between registered users of the system.

During the payment flow, the component:

- Converts the transfer amount to Serbian Dinars (RSD) using the current exchange rate provided by the National Bank of Serbia (NBS).
- Calculates a transaction fee depending on the transferred amount.
- Invokes the notification service to deliver a push notification to the participants of the transfer.
- Enriches the user-provided payment description through an LLM to produce a more readable transaction label that is stored with the payment.

## Usage

Call `PaymentService#processPayment` with the following parameters:

- `amount` — the amount of money to be transferred.
- `currency` — the currency in which the amount is specified.
- `recipientId` — the ID of the recipient user.
- `description` — free-form payment description provided by the sender; it is sent to the LLM for enrichment and stored with the payment.

The sender is resolved from the security context (the currently authenticated user).

The method handles currency conversion, description enrichment, fee calculation, persistence, and notification dispatch in a single call.

---

**Note:** This code review task is intended to be used during an interview. The candidate will be asked to review the provided code, identify any issues or improvements, and discuss their suggestions with the interviewer.