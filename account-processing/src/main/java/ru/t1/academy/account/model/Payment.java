package ru.t1.academy.account.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.t1.academy.account.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.t1.academy.account.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long accountId;

    @NotNull
    @Column(nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private boolean isCredit;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime payedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String type;
}
