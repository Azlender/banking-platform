package ru.t1.academy.client.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;

    private String login;
    private String password;
    private String email;

    private String documentType;  // <--- добавь это поле
    private String documentId;
    private String documentPrefix;
    private String documentSuffix;
}
