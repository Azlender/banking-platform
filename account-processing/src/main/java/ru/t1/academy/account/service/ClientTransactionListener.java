package ru.t1.academy.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.academy.account.dto.kafka.ClientTransactionEvent;
import ru.t1.academy.account.model.Account;
import ru.t1.academy.account.model.Payment;
import ru.t1.academy.account.model.Transaction;
import ru.t1.academy.account.model.enums.Status;
import ru.t1.academy.account.model.enums.TransactionStatus;
import ru.t1.academy.account.model.repository.AccountRepository;
import ru.t1.academy.account.model.repository.PaymentRepository;
import ru.t1.academy.account.model.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientTransactionListener {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;

    private static final int MAX_TRANSACTIONS = 5; // N
    private static final long TRANSACTION_WINDOW_MINUTES = 5; // T (в минутах)

    @KafkaListener(topics = "client_transactions", groupId = "account-processing")
    public void handleTransaction(ClientTransactionEvent event) {
        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Игнорируем заблокированные или арестованные счета
        if (account.getStatus() == Status.FROZEN || account.getStatus() == Status.ARRESTED) {
            return;
        }

        // Создаем запись о транзакции
        Transaction transaction = Transaction.builder()
                .accountId(account.getId())
                .cardId(event.getCardId())
                .type(event.getType())
                .amount(event.getAmount())
                .status(TransactionStatus.PROCESSING)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        // Проверка на превышение количества транзакций по одной карте
        if (event.getCardId() != null) {
            LocalDateTime windowStart = LocalDateTime.now().minusMinutes(TRANSACTION_WINDOW_MINUTES);
            List<Transaction> recentTransactions = transactionRepository.findByCardIdAndTimestampAfter(
                    event.getCardId(), windowStart);
            if (recentTransactions.size() > MAX_TRANSACTIONS) {
                account.setStatus(Status.FROZEN);
                accountRepository.save(account);
                transaction.setStatus(TransactionStatus.BLOCKED);
                transactionRepository.save(transaction);
                return;
            }
        }

        // Начисление/списание суммы
        switch (event.getType()) {
            case "DEPOSIT" -> account.setBalance(account.getBalance().add(event.getAmount()));
            case "WITHDRAW" -> {
                if (account.getBalance().compareTo(event.getAmount()) >= 0) {
                    account.setBalance(account.getBalance().subtract(event.getAmount()));
                } else {
                    transaction.setStatus(TransactionStatus.BLOCKED);
                    transactionRepository.save(transaction);
                    return;
                }
            }
        }

        // Обработка кредитных счетов с аннуитетом
        if (account.isRecalc()) {
            List<Payment> payments = paymentRepository.findByAccountId(account.getId());
            for (Payment payment : payments) {
                LocalDate dueDate = payment.getPaymentDate();
                if (!payment.isCredit()|| payment.getPayedAt() != null) continue;

                if (LocalDate.now().isEqual(dueDate) || LocalDate.now().isAfter(dueDate)) {
                    BigDecimal amountDue = payment.getAmount();
                    if (account.getBalance().compareTo(amountDue) >= 0) {
                        account.setBalance(account.getBalance().subtract(amountDue));
                        payment.setPayedAt(LocalDateTime.now());
                        paymentRepository.save(payment);
                    } else {
                        // Недостаточно средств, помечаем как просроченный
                        // Payment не изменяем payedAt
                    }
                }
            }
        }

        accountRepository.save(account);
        transaction.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(transaction);
    }
}
