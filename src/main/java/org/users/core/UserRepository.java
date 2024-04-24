package org.users.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.users.core.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
