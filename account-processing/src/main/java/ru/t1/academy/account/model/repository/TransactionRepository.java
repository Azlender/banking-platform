package ru.t1.academy.account.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.account.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCardIdAndTimestampAfter(Long cardId, LocalDateTime after);
}
