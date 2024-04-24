package org.users.core;


import jakarta.validation.ConstraintViolationException;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.users.core.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Value("${users.min-age}")
    int minAge;

    @Autowired
    UserService userService;

    @Test
    public void testSave_Valid() {
        var user = new User("mockemail@mock.com", "John", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var savedUser = userService.save(user);
        Assertions.assertEquals(user, savedUser);
    }

    private Stream<?> invalidUsers() {
        var invalidEmailUser = new User("mockemail", "John", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var invalidFirstNameUser = new User("mockemail@mock.com", "$#", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var invalidLastNameUser = new User("mockemail@mock.com", "John", "   ",
                LocalDate.now().minusYears(minAge + 10));
        var invalidBirthDateUser = new User("mockemail@mock.com", "John", "Doe",
                LocalDate.now());


        return Stream.of(
                new Pair<>(invalidEmailUser, List.of(
                        new ConstraintViolationInfo("email", "must be a well-formed email address")
                )),
                new Pair<>(invalidFirstNameUser, List.of(
                        new ConstraintViolationInfo("firstName", "must match \"^[a-zA-Z-]+$\"")
                )),
                new Pair<>(invalidLastNameUser, List.of(
                        new ConstraintViolationInfo("lastName", "must match \"^[a-zA-Z-]+$\""),
                        new ConstraintViolationInfo("lastName", "must not be blank")
                )),
                new Pair<>(invalidBirthDateUser, List.of(
                        new ConstraintViolationInfo("birthDate", "Must be a date in the past")
                ))
        );

    }

    public record ConstraintViolationInfo(String field, String message) {
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void testSave_Invalid(Pair<User, List<ConstraintViolationInfo>> userAndErrors) {
        var user = userAndErrors.a;
        var error = userAndErrors.b;

        var thrown = assertThrows(
                ConstraintViolationException.class,
                () -> userService.save(user)
        );
        var violations = thrown.getConstraintViolations();
        Assertions.assertEquals(error.size(), violations.size());
        assertThat(violations.stream().map(v ->
                new ConstraintViolationInfo(v.getPropertyPath().toString(), v.getMessage()))
        ).containsExactlyInAnyOrderElementsOf(error);
    }

}
