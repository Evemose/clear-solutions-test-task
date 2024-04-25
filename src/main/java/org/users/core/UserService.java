package org.users.core;

import jakarta.persistence.EntityNotFoundException;
import org.users.core.model.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Saves a user
     * @param user the user to save
     * @return the saved user
     * @throws NullPointerException if a user is null
     */
    User save(User user);

    /**
     * Checks if a user with the given id exists
     * @param id the id of the user to check
     * @return true if a user with the given id exists, false otherwise
     * @throws NullPointerException if id is null
     */
    boolean existsById(Long id);

    /**
     * Deletes a user by id
     * @param id the id of the user to delete
     * @throws EntityNotFoundException if the user with the given id does not exist
     * @throws NullPointerException if id is null
     */
    void deleteById(Long id);

    /**
     * Finds a user by id
     * @param id the id of the user to find
     * @return an optional containing the user with the given id, or empty if no such user exists
     * @throws NullPointerException if id is null
     */
    Optional<User> findById(Long id);

    /**
     * Finds all users with a birthdate between the given start and end dates (inclusive)
     * @param start the start date
     * @param end the end date
     * @return a list of users with a birthdate between the given start and end dates
     * @throws IllegalArgumentException if start is after end. More formally, if {@code start.isAfter(end)}
     * @throws NullPointerException if start or end is null
     */
    List<User> findByBirthdateBetween(LocalDate start, LocalDate end);
}
