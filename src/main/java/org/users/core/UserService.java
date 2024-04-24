package org.users.core;


import org.users.core.model.User;

public interface UserService {
    User save(User user);
    boolean existsById(Long id);
    void deleteById(Long id);
}
