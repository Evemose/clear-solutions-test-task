package org.users.core;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    @Delegate(types = ListCrudRepository.class)
    final UserRepository userRepository;
}
