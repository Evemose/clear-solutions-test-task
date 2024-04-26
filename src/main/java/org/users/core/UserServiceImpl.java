package org.users.core;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.users.core.model.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    @Override
    public User save(@NonNull User user) {
        return userRepository.saveAndFlush(user); // flush to return audited entity
    }

    @Override
    public boolean existsById(@NonNull Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByBirthdateBetween(@NonNull LocalDate start, @NonNull LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start date must be before end date");
        }
        return userRepository.findByBirthDateBetween(start, end);
    }
}
