package ru.t1.academy.account.dto.kafka;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCardEvent {
    private String clientId;
    private Long accountId;
    private String cardType;
}
