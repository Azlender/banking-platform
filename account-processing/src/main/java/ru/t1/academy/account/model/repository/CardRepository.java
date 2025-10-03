package ru.t1.academy.account.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.account.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}