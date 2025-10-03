package ru.t1.academy.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.credit.model.ProductRegistry;
import java.util.List;

public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, Long> {
    List<ProductRegistry> findByClientId(Long clientId);
}