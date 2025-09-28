package ru.t1.academy.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.credit.model.PaymentRegistry;
import ru.t1.academy.credit.model.ProductRegistry;

import java.math.BigDecimal;

public interface PaymentRegistryRepository extends JpaRepository<PaymentRegistry, Long> {
    boolean existsByProductRegistryAndExpiredTrue(ProductRegistry productRegistry);

    default BigDecimal sumDebtByProductId(Long productId) {
        // Простейшая заглушка, можно сделать JPQL или SQL sum
        return BigDecimal.ZERO;
    }
}
