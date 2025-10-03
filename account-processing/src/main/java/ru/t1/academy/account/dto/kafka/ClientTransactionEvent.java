package ru.t1.academy.account.dto.kafka;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientTransactionEvent {
    private Long accountId;
    private Long cardId;      // может быть null
    private String type;      // DEPOSIT, WITHDRAW, TRANSFER
    private BigDecimal amount;
}
