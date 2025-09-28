package ru.t1.academy.client.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreationRequest {
    private String clientId;
    private Long accountId;
    private String cardType; // DC, CC и т.д.
}
