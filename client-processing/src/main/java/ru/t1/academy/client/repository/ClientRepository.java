package ru.t1.academy.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.client.model.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    // добавляем метод поиска по clientId
    Optional<Client> findByClientId(Long clientId);
}
