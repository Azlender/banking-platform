package ru.t1.academy.account.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}