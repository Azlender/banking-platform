package ru.t1.academy.account.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentEvent {
    private UUID key;        // ключ сообщения
    private Long accountId;  // счет
    private BigDecimal amount; // сумма платежа
    private String type;     // тип транзакции
}
