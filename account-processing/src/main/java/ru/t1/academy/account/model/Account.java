package ru.t1.academy.account.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.t1.academy.account.model.enums.Status;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private int clientId;

    @NotNull
    @Column(nullable = false)
    private Long productId;

    @NotNull
    @Column(nullable = false)
    private BigDecimal balance;

    @NotNull
    @Column(nullable = false)
    private int interestRate;

    @NotNull
    @Column(nullable = false)
    private boolean isRecalc;

    @NotNull
    @Column(nullable = false)
    private boolean cardExist;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
