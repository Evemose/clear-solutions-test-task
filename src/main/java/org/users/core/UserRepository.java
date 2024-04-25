package org.users.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.users.core.model.entities.User;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByBirthDateBetween(LocalDate from, LocalDate to);
}
