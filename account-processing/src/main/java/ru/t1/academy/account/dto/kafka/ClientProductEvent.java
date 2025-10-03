package ru.t1.academy.account.dto.kafka;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProductEvent {
    private Long clientId;
    private Long productId;
    private String key;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String status;
}
