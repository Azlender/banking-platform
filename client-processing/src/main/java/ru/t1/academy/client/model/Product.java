package ru.t1.academy.client.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.t1.academy.client.model.enums.ProductKey;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductKey key;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false, unique = true)
    private String productId;

    @PostPersist
    private void generateProductId() {
        if (this.id != null && this.key != null) {
            this.productId = key.name() + this.id;
        }
    }
}
