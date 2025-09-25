package ru.t1.academy.client.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.t1.academy.client.model.enums.DocumentType;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 12)
    private String clientId; // формат XXFFNNNNNNNN

    // Many Clients → One User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @Past
    @NotNull
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @NotBlank
    @Column(nullable = false)
    private String documentId;

    private String documentPrefix;

    private String documentSuffix;
}
