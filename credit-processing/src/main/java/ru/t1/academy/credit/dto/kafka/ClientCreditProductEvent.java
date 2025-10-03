package ru.t1.academy.credit.dto.kafka;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreditProductEvent {
    private Long clientId;
    private Long productId;
    private Long accountId;
    private BigDecimal amount;   // сумма кредита
    private Integer interestRate; // годовая ставка
    private Integer monthCount;  // количество месяцев
    private LocalDate openDate;
}
