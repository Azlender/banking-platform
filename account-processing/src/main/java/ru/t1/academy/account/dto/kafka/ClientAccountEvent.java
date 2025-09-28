package ru.t1.academy.account.dto.kafka;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAccountEvent {
    private int clientId;
    private Long accountId;
    private Long productId;
    private BigDecimal balance;
}
