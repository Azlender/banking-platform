package ru.t1.academy.account.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.account.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccountId(Long accountId);
}
