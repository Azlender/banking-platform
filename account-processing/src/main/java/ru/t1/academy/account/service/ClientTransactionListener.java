package ru.t1.academy.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.academy.account.dto.kafka.ClientTransactionEvent;
import ru.t1.academy.account.model.Account;
import ru.t1.academy.account.model.Transaction;
import ru.t1.academy.account.model.enums.TransactionStatus;
import ru.t1.academy.account.model.repository.AccountRepository;
import ru.t1.academy.account.model.repository.TransactionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientTransactionListener {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "client_transactions", groupId = "account-processing")
    public void handleTransaction(ClientTransactionEvent event) {
        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Создаём запись о транзакции
        Transaction transaction = Transaction.builder()
                .accountId(account.getId())
                .cardId(event.getCardId())
                .type(event.getType())
                .amount(event.getAmount())
                .status(TransactionStatus.PROCESSING)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // Простая обработка (DEPOSIT/ WITHDRAW)
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

        accountRepository.save(account);

        // Обновляем статус транзакции
        transaction.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(transaction);
    }
}
