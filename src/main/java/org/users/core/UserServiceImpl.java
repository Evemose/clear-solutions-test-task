package org.users.core;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.users.core.model.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "user must not be null");
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return userRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        if (!existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByBirthdateBetween(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start date must not be null");
        Objects.requireNonNull(end, "end date must not be null");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start date must be before end date");
        }
        return userRepository.findByBirthDateBetween(start, end);
    }
}
