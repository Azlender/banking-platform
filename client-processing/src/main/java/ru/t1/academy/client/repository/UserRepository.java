package ru.t1.academy.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.academy.client.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}