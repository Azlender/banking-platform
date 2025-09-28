package ru.t1.academy.account.dto.kafka;

import lombok.*;
import ru.t1.academy.client.model.enums.ClientProductStatus;
import ru.t1.academy.client.model.enums.ProductKey;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProductEvent {
    private int clientId;       // числовой clientId
    private Long productId;
    private ProductKey key;
    private LocalDate openDate;
    private LocalDate closeDate;
    private ClientProductStatus status;
}
