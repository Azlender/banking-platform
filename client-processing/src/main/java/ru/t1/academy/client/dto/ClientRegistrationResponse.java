package ru.t1.academy.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationResponse {
    private Long userId;
    private String login;
    private String email;
    private String clientId;
}
