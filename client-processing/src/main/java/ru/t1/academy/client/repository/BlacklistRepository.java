package ru.t1.academy.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.client.model.BlacklistRegistry;

import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistRegistry, Long> {
    boolean existsByDocumentTypeAndDocumentId(String documentType, String documentId);
}
