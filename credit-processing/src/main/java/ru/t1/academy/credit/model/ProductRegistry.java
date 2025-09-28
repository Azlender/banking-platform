package ru.t1.academy.credit.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "product_registry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer interestRate;

    @Column(nullable = false)
    private LocalDate openDate;

    @Column(nullable = false)
    private Integer monthCount;
}
